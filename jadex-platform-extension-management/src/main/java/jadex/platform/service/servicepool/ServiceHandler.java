package jadex.platform.service.servicepool;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IPoolStrategy;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  The service handler is used as service implementation for proxy services.
 *  Incoming calls will be served by instances from the pool.
 */
@Service
public class ServiceHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/** List of idle services. */
	protected Map<IService, ITimer> servicepool;

	/** A queue of open requests. */
	protected List<Object[]> queue;
	
	/** The strategy. */
	protected IPoolStrategy strategy;
	
	/** The worker component name. */
	protected String componentname;
	
	/** The clock service. */
	protected IClockService clock;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service handler.
	 */
	public ServiceHandler(IInternalAccess component, Class<?> servicetype, 
		IPoolStrategy strategy, String componentname)
	{
		this.component = component;
		this.servicetype = servicetype;
		this.strategy = strategy;
		this.componentname = componentname;
		this.servicepool = new LinkedHashMap<IService, ITimer>();
		this.queue = new LinkedList<Object[]>();
	}
	
	//-------- methods --------

	/**
	 *  Callback of the invocation handler interface.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		assert component.isComponentThread();
		
		if(!SReflect.isSupertype(IFuture.class, method.getReturnType()))
			return new Future<Object>(new IllegalArgumentException("Return type must be future: "+method.getName()));
		
		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null));
		
		// Add task to queue.
		queue.add(new Object[]{method, args, ret});
		// Notify strategy that task was added
		boolean create = strategy.taskAdded();
		
		// Create new service if necessary
		if(create)
		{
			component.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
			{
				public void customResultAvailable(final IComponentManagementService cms)
				{
					CreationInfo ci  = new CreationInfo(component.getComponentIdentifier());
					ci.setImports(component.getModel().getAllImports());
					cms.createComponent(null, componentname, ci, null)
						.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Object>(ret)
					{
						public void customResultAvailable(IComponentIdentifier result)
						{
							cms.getExternalAccess(result)
								.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
							{
								public void customResultAvailable(IExternalAccess ea)
								{
									Future<Object> fut = (Future<Object>)SServiceProvider.getService(ea.getServiceProvider(), servicetype, RequiredServiceInfo.SCOPE_LOCAL);
									fut.addResultListener(component.createResultListener(new DelegationResultListener<Object>(ret)
									{
										public void customResultAvailable(Object service)
										{
											addFreeService((IService)service);
										}
									}));
								}
							});
						};
					});
				}
			});
		}
		else
		{
			addFreeService(null);
		}
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Called when a service becomes idle.
	 */
	protected void	addFreeService(IService service)
	{
		assert component.isComponentThread();

		// Invoke a service if there is a task and a free worker
		if(!queue.isEmpty())
		{
			if(service==null && !servicepool.isEmpty())
			{
				service = servicepool.keySet().iterator().next();
				ITimer timer = servicepool.remove(service);
				if(timer!=null)
				{
//					System.out.println("cancelled timer for: "+service);
					timer.cancel();
				}
			}
			
			if(service!=null)
			{
				final Object[] task = queue.remove(0);
				Method method = (Method)task[0];
				Object[] args = (Object[])task[1];
				Future<?> ret = (Future<?>)task[2];
				invokeService(service, method, args, ret);
			}
		}
		else if(service!=null)
		{
			// add service to pool and initiate timer
			updateWorkerTimer(service).addResultListener(new DefaultResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					// nop
				}
			});
		}
	}
	
	/**
	 *  Update the worker timer by:
	 *  - creating a timer (if timeout)
	 *  - update the service pool entry for the service (service, timer)
	 */
	protected IFuture<Void> updateWorkerTimer(final IService service)
	{
		assert component.isComponentThread();
		
		final Future<Void> ret = new Future<Void>();
		
		if(strategy.getWorkerTimeout()>0)
		{
			// Add service with timer to pool
			createTimer(strategy.getWorkerTimeout(), new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
					component.getExternalAccess().scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							// When timer triggers check that pool contains service and remove it
							if(servicepool.containsKey(service))
							{
								boolean remove = strategy.workerTimeoutOccurred();
								if(remove)
								{
//									System.out.println("timeout of worker: "+service);
									servicepool.remove(service);
									removeService(service);
								}
								else
								{
									// add service to pool and initiate timer
									updateWorkerTimer(service).addResultListener(new DefaultResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											// nop
										}
									});
								}
							}
//							else
//							{
//								System.out.println("timer occurred but service not in pool: "+service+" "+servicepool);
//							}
							return IFuture.DONE;
						}
					});
				}
			}).addResultListener(new ExceptionDelegationResultListener<ITimer, Void>(ret)
			{
				public void customResultAvailable(ITimer timer)
				{
					servicepool.put(service, timer);
					ret.setResult(null);
				}
			});
		}
		else
		{
			servicepool.put(service, null);
		}
		
		return ret;
	}
	
	/**
	 *  Execute a task on a service.
	 */
	protected void invokeService(final IService service, Method method, Object[] args, Future<?> ret)
	{
		assert component.isComponentThread();
		
		try
		{
			IFuture<Object> res = (IFuture<Object>)method.invoke(service, args);
			FutureFunctionality.connectDelegationFuture(ret, res);
			
			// put the components back in pool after call is done
			res.addResultListener(new IResultListener<Object>()
			{
				public void resultAvailable(Object result)
				{
					proceed();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Exception during service invocation in service pool:_"+exception.getMessage());
//					exception.printStackTrace();
					proceed();
				}
				
				protected void proceed()
				{
					boolean remove = strategy.taskFinished();
					if(remove)
					{
						addFreeService(null);
						removeService(service).addResultListener(new DefaultResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								// nop
							}
						});
					}
					else
					{
						addFreeService(service);
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
	}
	
	/**
	 *  Remove a service and the worker.
	 */
	protected IFuture<Void> removeService(IService service)
	{
		final Future<Void> ret = new Future<Void>();
		
		final IComponentIdentifier workercid = service.getServiceIdentifier().getProviderId();
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(component.getServiceContainer(), 
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		fut.addResultListener(component.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.destroyComponent(workercid).addResultListener(
					component.createResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
				{
					public void customResultAvailable(Map<String, Object> result) 
					{
//						System.out.println("removed worker: "+workercid);
						System.out.println("strategy state: "+strategy);
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get the clockservice (cached).
	 */
	protected IFuture<IClockService> getClockService()
	{
		final Future<IClockService> ret = new Future<IClockService>();
		
		if(clock!=null)
		{
			ret.setResult(clock);
		}
		else
		{
			SServiceProvider.getService(component.getServiceContainer(), 
				IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener<IClockService>(ret)
			{
				public void customResultAvailable(IClockService cs)
				{
					clock = cs;
					ret.setResult(clock);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Create a timer via the clock service.
	 */
	protected IFuture<ITimer> createTimer(final long delay, final ITimedObject to)
	{
		final Future<ITimer> ret = new Future<ITimer>();
		
		getClockService().addResultListener(new ExceptionDelegationResultListener<IClockService, ITimer>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				ret.setResult(cs.createTimer(delay, to));
			}
		});
		
		return ret;
	}
}
