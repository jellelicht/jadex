package jadex.bridge.service.search;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.search.IRankingSearchTerminationDecider;
import jadex.bridge.nonfunctional.search.IServiceRanker;
import jadex.bridge.nonfunctional.search.ServiceRankingDelegationResultListener;
import jadex.bridge.nonfunctional.search.ServiceRankingDelegationResultListener2;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Static helper class for searching services.
 */
public class SServiceProvider
{	
	/** The reference method cache (method -> boolean[] (is reference)). */
	public static final Map methodreferences = Collections.synchronizedMap(new LRU(500));

	//-------- sync method (only local search) --------
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(IComponentIdentifier component, Class<T> type)
	{
		return getLocalService(component, type, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(IComponentIdentifier component, Class<T> type, final String scope)
	{
		return getLocalService(component, type, scope, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(IComponentIdentifier component, final Class<T> type, final String scope, final IFilter<T> filter)
	{
		return SynchronizedServiceRegistry.getRegistry(component.getRoot()).searchService(type, component, scope, filter);
	}
	
	/**
	 *  Get one service of a type. 
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type)
	{
		return getLocalService(component, type, (String)null, true);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, final String scope)
	{
		return getLocalService(component, type, scope, null, true);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param component The internal access.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, final String scope, final IFilter<T> filter)
	{
		return getLocalService(component, type, scope, filter, true);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param component The internal access.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, final IComponentIdentifier target)
	{
		return getLocalService(component, type, target, true);
	}

	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess component, final Class<T> type)
	{
		return getLocalServices(component, type, null, true);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess component, final Class<T> type, final String scope)
	{
		return getLocalServices(component, type, scope, null, true);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess component, final Class<T> type, final String scope, final IFilter<T> filter)
	{
		return getLocalServices(component, type, scope, filter, true);
	}
	
	/**
	 *  Get one service of a type. 
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, boolean proxy)
	{
		return getLocalService(component, type, (String)null, proxy);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, final String scope, boolean proxy)
	{
		return getLocalService(component, type, scope, null, proxy);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param component The internal access.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, final String scope, final IFilter<T> filter, boolean proxy)
	{
		checkThreadAccess(component, proxy);
		
		T ret = SynchronizedServiceRegistry.getRegistry(component).searchService(type, component.getComponentIdentifier(), scope, filter);
		if(ret==null)
			throw new ServiceNotFoundException(type.getName());
		return proxy? createRequiredProxy(component, ret, type): ret;
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param component The internal access.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService0(final IInternalAccess component, final Class<T> type, final String scope, final IFilter<T> filter, boolean proxy)
	{
		checkThreadAccess(component, proxy);
		
		T ret = SynchronizedServiceRegistry.getRegistry(component).searchService(type, component.getComponentIdentifier(), scope, filter);
		return proxy && ret!=null? createRequiredProxy(component, ret, type): ret;
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param component The internal access.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess component, final Class<T> type, final IComponentIdentifier target, boolean proxy)
	{
		checkThreadAccess(component, proxy);

		T ret = SynchronizedServiceRegistry.getRegistry(component).searchService(type, component.getComponentIdentifier(), RequiredServiceInfo.SCOPE_PLATFORM, new IFilter<T>() 
		{
			public boolean filter(T obj) 
			{
				return ((IService)obj).getServiceIdentifier().getProviderId().equals(target);
			}
		});
		if(ret==null)
			throw new ServiceNotFoundException(type.getName());
		
		return proxy? createRequiredProxy(component, ret, type): ret;
	}

	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess component, final Class<T> type, boolean proxy)
	{
		return getLocalServices(component, type, null, proxy);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess component, final Class<T> type, final String scope, boolean proxy)
	{
		return getLocalServices(component, type, scope, null, proxy);
	}
	
	/**
	 *  Get one service of a type.
	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess component, final Class<T> type, final String scope, final IFilter<T> filter, boolean proxy)
	{
		checkThreadAccess(component, proxy);

		Collection<T> ret = SynchronizedServiceRegistry.getRegistry(component).searchServices(type, component.getComponentIdentifier(), scope, filter);
		
		// Fixing the bug by changing createRequiredProxy -> createRequiredProxies leads to not compiling the main class
		return proxy? createRequiredProxies(component, ret, type): ret;
	}
	
	
	//-------- async methods --------

	/**
	 *  Get one service of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IInternalAccess component, Class<T> type)
	{
		return getService(component, type, null, true);
	}
	
	/**
	 *  Get one service of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IInternalAccess component, Class<T> type, String scope)
	{
		return getService(component, type, scope, (IAsyncFilter<T>)null, true);
	}
	
	/**
	 *  Get one service of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final Class<T> type, final String scope, final IAsyncFilter<T> filter)
	{
		return getService(component, type, scope, filter, true);
	}
	
	/**
	 *  Get one service with id.
//	 *  (Returns required service proxy).
	 *  @param clazz The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final IServiceIdentifier sid)
	{
		return getService(component, sid, true);
	}
	
	/**
	 *  Get a service from a specific component.
//	 *  (Returns required service proxy).
	 *  @param component The component.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final IComponentIdentifier cid, final Class<T> type)
	{
		return getService(component, cid, type, true);
	}
	
	/**
	 *  Get all services of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess component, Class<T> type)
	{
		return getServices(component, type, null, true);
	}
	
	/**
	 *  Get all services of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess component, Class<T> type, String scope)
	{
		return getServices(component, type, scope, (IAsyncFilter<T>)null, true);
	}
	
	/**
	 *  Get all services of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess component, Class<T> type, String scope, IAsyncFilter<T> filter)
	{
		return getServices(component, type, scope, filter, true);
	}
	
//	/**
//	 *  Get one service of a type and only search upwards (parents).
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static <T> IFuture<T> getServiceUpwards(IInternalAccess provider, Class<T> type)
//	{
//		return getService(provider, type, RequiredServiceInfo.SCOPE_UPWARDS);
//	}
	
	/**
	 *  Get all declared services of the given provider.
//	 *  (Returns required service proxy).
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getDeclaredService(IInternalAccess component, Class<T> type)
	{
		return getService(component, type, RequiredServiceInfo.SCOPE_LOCAL, true);
	}
	
	/**
	 *  Get all declared services of the given provider.
//	 *  (Returns required service proxy).
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture<IService> getDeclaredServices(IInternalAccess component)
	{
		return getDeclaredServices(component, true);
	}
	
	
	/**
	 *  Get one service of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IInternalAccess component, Class<T> type, boolean proxy)
	{
		return getService(component, type, null, proxy);
	}
	
	/**
	 *  Get one service of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IInternalAccess component, Class<T> type, String scope, boolean proxy)
	{
		return getService(component, type, scope, (IAsyncFilter<T>)null, proxy);
	}
	
	/**
	 *  Get one service of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final Class<T> type, final String scope, final IAsyncFilter<T> filter, final boolean proxy)
	{
		final Future<T> ret = new Future<T>();
		
		ensureThreadAccess(component, proxy).addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(!RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
				{
					if(filter==null)
					{
						IServiceRegistry reg = ServiceRegistry.getRegistry(component);
						T ser = reg==null? null: reg.searchService(type, component.getComponentIdentifier(), scope);
//						T ser = PlatformServiceRegistry.getRegistry(component).searchService(type, component.getComponentIdentifier(), scope);
						if(ser!=null)
						{
							if(proxy)
								ser = createRequiredProxy(component, ser, type);
							ret.setResult(ser);
						}
						else
						{
							ret.setException(new ServiceNotFoundException(type.getName()));
//							{
//								public void printStackTrace(java.io.PrintStream s)
//								{
//									Thread.dumpStack();
//									super.printStackTrace(s);
//								}
//								
//								public void printStackTrace(java.io.PrintWriter s)
//								{
//									Thread.dumpStack();
//									super.printStackTrace(s);									
//								}
//								
//								public void printStackTrace()
//								{
//									Thread.dumpStack();
//									super.printStackTrace();
//								}
//							});
						}
					}
					else
					{
						IResultListener<T> lis = proxy? new ProxyResultListener<T>(ret, component, type): new DelegationResultListener<T>(ret);
						SynchronizedServiceRegistry.getRegistry(component).searchService(type, component.getComponentIdentifier(), scope, filter)
							.addResultListener(new ComponentResultListener<T>(lis, component));
					}
				}
				else
				{
					IResultListener<T> lis = proxy? new ProxyResultListener<T>(ret, component, type): new DelegationResultListener<T>(ret);
					SynchronizedServiceRegistry.getRegistry(component).searchGlobalService(type, component.getComponentIdentifier(), filter)
						.addResultListener(new ComponentResultListener<T>(lis, component));
				}
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Get one service with id.
////	 *  (Returns required service proxy).
//	 *  @param clazz The class.
//	 *  @return The corresponding service.
//	 */
//	public static <T> IFuture<T> getService(final IInternalAccess component, final IServiceIdentifier sid, final boolean proxy)
//	{
//		final Future<T> ret = new Future<T>();
//		
//		ensureThreadAccess(component, proxy).addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				if(sid.getProviderId().equals(component.getComponentIdentifier()))
//				{
//					T ser = (T)component.getComponentFeature(IProvidedServicesFeature.class)
//						.getProvidedService(sid.getServiceName());
//					if(proxy)
//						ser = createRequiredProxy(component, ser, sid.getServiceType().getType(component.getClassLoader()));
//					ret.setResult(ser);
//				}
//				else
//				{
//					SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
//					{
//						public void customResultAvailable(IComponentManagementService cms)
//						{
//							cms.getExternalAccess(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
//							{
//								public void customResultAvailable(IExternalAccess ea)
//								{
//									IResultListener<T> lis = proxy? new ProxyResultListener<T>(ret, component, sid.getServiceType().getType(component.getClassLoader())): new DelegationResultListener<T>(ret);
//		
//									ea.scheduleStep(new ImmediateComponentStep<T>()
//									{
//										@Classname("getService(final IInternalAccess provider, final IServiceIdentifier sid)")
//		
//										public IFuture<T> execute(IInternalAccess ia)
//										{
//											return getService(ia, sid, false);
//										}
//									}).addResultListener(new ComponentResultListener<T>(lis, component));
//								}
//							});
//						}
//					});
//				}
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Get a service from a specific component.
//	 *  (Returns required service proxy).
	 *  @param component The component.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final IComponentIdentifier cid, final Class<T> type, final boolean proxy)
	{
		return getService(component, cid, RequiredServiceInfo.SCOPE_LOCAL, type, proxy);
	}
	
	/**
	 *  Get a service from a specific component.
//	 *  (Returns required service proxy).
	 *  @param component The component.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final IComponentIdentifier cid, final String scope, final Class<T> type, final boolean proxy)
	{
		final Future<T> ret = new Future<T>();
		
		ensureThreadAccess(component, proxy).addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// component itself?
				if(cid.equals(component.getComponentIdentifier()))
				{
					T res = (T)component.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(type);
					if(res!=null)
					{
						if(proxy)
							res = createRequiredProxy(component, res, type);
						ret.setResult(res);
					}
					else
					{
						ret.setException(new ServiceNotFoundException(""+type));
					}
				}
				
				// local component?
				else if(cid.getRoot().equals(component.getComponentIdentifier().getRoot()))
				{
					SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
					{
						public void customResultAvailable(IComponentManagementService cms)
						{
							cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
							{
								public void customResultAvailable(IExternalAccess ea)
								{
									IResultListener<T> lis = proxy? new ProxyResultListener<T>(ret, component, type): new DelegationResultListener<T>(ret);
									
									final IComponentIdentifier	fcid	= cid;
									final Class<T>	ftype	= type;
									
									ea.scheduleStep(new ImmediateComponentStep<T>()
									{
										@Classname("getService(final IInternalAccess provider, final IComponentIdentifier cid, final Class<T> type)")
										
										public IFuture<T> execute(IInternalAccess ia)
										{
											return getService(ia, fcid, ftype, false);
										}
									}).addResultListener(new ComponentResultListener<T>(lis, component));
								}
							});
						}
					});
				}
					
				// For remote use rms, to allow correct security settings due to not using getExternalAccess()
				else
				{
					IResultListener<T> lis = proxy? new ProxyResultListener<T>(ret, component, type): new DelegationResultListener<T>(ret);
					
					IRemoteServiceManagementService rms	= SServiceProvider.getLocalService(component, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
					rms.getServiceProxy(component.getComponentIdentifier(), cid, type, scope, null)
						.addResultListener(new ComponentResultListener<T>(lis, component));
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get a service from a specific component.
//	 *  (Returns required service proxy).
	 *  @param component The component.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess component, final IServiceIdentifier sid, final boolean proxy)
	{
		final Future<T> ret = new Future<T>();
		final IComponentIdentifier cid = sid.getProviderId();
		
		ensureThreadAccess(component, proxy).addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// component itself?
				if(cid.equals(component.getComponentIdentifier()))
				{
					T res = (T)component.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(sid);
					if(res!=null)
					{
						if(proxy)
							res = createRequiredProxy(component, res, sid.getServiceType().getType(component.getClassLoader()));
						ret.setResult(res);
					}
					else
					{
						ret.setException(new ServiceNotFoundException(""+sid));
					}
				}
				
				// local component?
				else if(cid.getRoot().equals(component.getComponentIdentifier().getRoot()))
				{
					SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
					{
						public void customResultAvailable(IComponentManagementService cms)
						{
							cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
							{
								public void customResultAvailable(IExternalAccess ea)
								{
									IResultListener<T> lis = proxy? 
										new ProxyResultListener<T>(ret, component, sid.getServiceType().getType(component.getClassLoader()))
										: new DelegationResultListener<T>(ret);
										
									final IServiceIdentifier fsid = sid;
									
									ea.scheduleStep(new ImmediateComponentStep<T>()
									{
										@Classname("getService(final IInternalAccess provider, final IComponentIdentifier cid, final Class<T> type)")
										
										public IFuture<T> execute(IInternalAccess ia)
										{
											return getService(ia, fsid, false);
										}
									}).addResultListener(new ComponentResultListener<T>(lis, component));
								}
							});
						}
					});
				}
					
				// For remote use rms, to allow correct security settings due to not using getExternalAccess()
				else
				{
					Class<T> type = (Class<T>)sid.getServiceType().getType(component.getClassLoader());
					IResultListener<T> lis = proxy? new ProxyResultListener<T>(ret, component, type): new DelegationResultListener<T>(ret);
					
					IRemoteServiceManagementService rms	= SServiceProvider.getLocalService(component, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
					
					final IServiceIdentifier fsid = sid;
					
					rms.getServiceProxy(component.getComponentIdentifier(), cid, type, RequiredServiceInfo.SCOPE_LOCAL, new IAsyncFilter<T>()
					{
						@Classname("getServicePerServiceIdentifier")
						public IFuture<Boolean> filter(T obj)
						{
							boolean ret = ((IService)obj).getServiceIdentifier().equals(fsid);
							return ret? Future.TRUE: Future.FALSE;
						}
					}).addResultListener(new ComponentResultListener<T>(lis, component));
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get all services of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess component, Class<T> type, boolean proxy)
	{
		return getServices(component, type, null, proxy);
	}
	
	/**
	 *  Get all services of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess component, Class<T> type, String scope, boolean proxy)
	{
		return getServices(component, type, scope, (IAsyncFilter<T>)null, proxy);
	}
	
	/**
	 *  Get all services of a type.
//	 *  (Returns required service proxy).
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(final IInternalAccess component, final Class<T> type, final String scope, final IAsyncFilter<T> filter, final boolean proxy)
	{
		final TerminableIntermediateDelegationFuture<T> ret = new TerminableIntermediateDelegationFuture<T>();
		
		ensureThreadAccess(component, proxy).addResultListener(new ExceptionDelegationResultListener<Void, Collection<T>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(!RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
				{
					if(filter==null)
					{
						Collection<T> sers = SynchronizedServiceRegistry.getRegistry(component).searchServices(type, component.getComponentIdentifier(), scope);
						if(proxy)
							sers = createRequiredProxies(component, sers, type);
						ret.setResult(sers==null? Collections.EMPTY_SET: sers);
					}
					else
					{
						IIntermediateResultListener<T> lis = proxy? new IntermediateProxyResultListener<T>(ret, component, type): new IntermediateDelegationResultListener<T>(ret); 
						SynchronizedServiceRegistry.getRegistry(component).searchServices(type, component.getComponentIdentifier(), scope, filter)
							.addResultListener(new IntermediateComponentResultListener<T>(lis, component));
					}
				}
				else
				{
					IIntermediateResultListener<T> lis = proxy? new IntermediateProxyResultListener<T>(ret, component, type): new IntermediateDelegationResultListener<T>(ret); 
					ISubscriptionIntermediateFuture<T> fut = SynchronizedServiceRegistry.getRegistry(component).searchGlobalServices(type, component.getComponentIdentifier(), filter);
					fut.addResultListener(new IntermediateComponentResultListener<T>(lis, component));
				}
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Get one service of a type and only search upwards (parents).
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static <T> IFuture<T> getServiceUpwards(IInternalAccess provider, Class<T> type)
//	{
//		return getService(provider, type, RequiredServiceInfo.SCOPE_UPWARDS);
//	}
	
	/**
	 *  Get all declared services of the given provider.
//	 *  (Returns required service proxy).
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getDeclaredService(IInternalAccess component, Class<T> type, boolean proxy)
	{
		return getService(component, type, RequiredServiceInfo.SCOPE_LOCAL, proxy);
	}
	
	/**
	 *  Get all declared services of the given provider.
//	 *  (Returns required service proxy).
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture<IService> getDeclaredServices(final IInternalAccess component, final boolean proxy)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		ensureThreadAccess(component, proxy).addResultListener(new ExceptionDelegationResultListener<Void, Collection<IService>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				try
				{
					for(Object s: component.getComponentFeature(IProvidedServicesFeature.class).getProvidedServices(null))
					{
						if(proxy)
						{
							s = createRequiredProxy(component, (IService)s, 
								((IService)s).getServiceIdentifier().getServiceType().getType(component.getClassLoader()));
						}
						ret.addIntermediateResult((IService)s);
					}
					ret.setFinished();
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
			}
		});
		
		return ret;
	}
	
	//-------- external access method duplicates --------
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, Class<T> type)
	{
		return getService(provider, type, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, Class<T> type, String scope)
	{
		return getService(provider, type, scope, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)
	{
		return provider.scheduleStep(new ImmediateComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, type, scope, filter, false);
			}
		});
	}
	
	/**
	 *  Get one service with id.
	 *  @param clazz The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, final IServiceIdentifier sid)
	{
		return provider.scheduleStep(new ImmediateComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final IServiceIdentifier sid)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, sid, false);
			}
		});
	}
	
	/**
	 *  Get a service from a specific component.
	 *  @param access The external access.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess access, final IComponentIdentifier cid, final Class<T> type)
	{
		return access.scheduleStep(new ImmediateComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final IComponentIdentifier cid, final Class<T> type)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, cid, type, false);
			}
		});
	}
	
	/**
	 *  Get a service from a specific component with defined scope.
	 *  @param access The external access.
	 *  @param cid The target component identifier.
	 *  @param scope The search scope.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess access, final IComponentIdentifier cid, final String scope, final Class<T> type)
	{
		return access.scheduleStep(new ImmediateComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final IComponentIdentifier cid, final String scope, final Class<T> type)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, cid, scope, type, false);
			}
		});
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IExternalAccess provider, Class<T> type)
	{
		return getServices(provider, type, null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IExternalAccess provider, Class<T> type, String scope)
	{
		return getServices(provider, type, scope, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)
	{
		return (ITerminableIntermediateFuture<T>)provider.scheduleStep(new ImmediateComponentStep<Collection<T>>()
		{
			@Classname("getServices(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)")
			public ITerminableIntermediateFuture<T> execute(IInternalAccess ia)
			{
				return getServices(ia, type, scope, filter);
			}
		});
	}
	
//	/**
//	 *  Get one service of a type and only search upwards (parents).
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static <T> IFuture<T> getServiceUpwards(IExternalAccess provider, Class<T> type)
//	{
//		return getService(provider, type, RequiredServiceInfo.SCOPE_UPWARDS);
//	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getDeclaredService(IExternalAccess provider, Class<T> type)
	{
		return getService(provider, type, RequiredServiceInfo.SCOPE_LOCAL);
	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture<IService> getDeclaredServices(IExternalAccess provider)
	{
		return (IIntermediateFuture<IService>)provider.scheduleStep(new ImmediateComponentStep<Collection<IService>>()
		{
			@Classname("getDeclaredServices(IExternalAccess provider)")
			public IIntermediateFuture<IService> execute(IInternalAccess ia)
			{
				return getDeclaredServices(ia, false);
			}
		});
	}

	/**
	 *  Get a service of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getTaggedService(IExternalAccess provider, final Class<T> type, final String scope, final String... tags)
	{
		return (IFuture<T>)provider.scheduleStep(new ImmediateComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final Class<T> type, final String scope, final String... args)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getTaggedService(ia, type, scope, tags);
			}
		});
	}
	
	/**
	 *  Find service by type and tags. Service must have all the tags.
	 *  @param component The component.
	 *  @param type The service type.
	 *  @param scope The search scope.
	 *  @param tags The tags.
	 *  @return A matching service
	 */
	public static <T> IFuture<T> getTaggedService(final IInternalAccess component, Class<T> type, String scope, final String... tags)
	{
		return getService(component, type, scope, new TagFilter<T>(component.getExternalAccess(), tags));
	}
	
	/**
	 *  Get all services of a type and tags. Services must have all the tags.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getTaggedServices(IExternalAccess component, final Class<T> type, final String scope, final String... tags)
	{
		return (ITerminableIntermediateFuture<T>)component.scheduleStep(new ImmediateComponentStep<Collection<T>>()
		{
			@Classname("getServices(IExternalAccess provider, final Class<T> type, final String scope, final String... args)")
			public ITerminableIntermediateFuture<T> execute(IInternalAccess ia)
			{
				return getTaggedServices(ia, type, scope, tags);
			}
		});
	}
	
	/**
	 *  Find services by type and tags. Service must have all the tags.
	 *  @param component The component.
	 *  @param type The service type.
	 *  @param scope The search scope.
	 *  @param tags The tags.
	 *  @return A matching service
	 */
	public static <T> ITerminableIntermediateFuture<T> getTaggedServices(final IInternalAccess component, Class<T> type, String scope, final String... tags)
	{
		return getServices(component, type, scope, new TagFilter<T>(component.getExternalAccess(), tags));
	}
	
	//-------- other methods --------
	
	/**
	 *  Rank the services of a search with a specific ranker.
	 */
	public static <S> ITerminableIntermediateFuture<S> rankServices(ITerminableIntermediateFuture<S> searchfut, 
		IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		TerminableIntermediateDelegationFuture<S> ret = new TerminableIntermediateDelegationFuture<S>();
		searchfut.addResultListener(new ServiceRankingDelegationResultListener<S>(ret, searchfut, ranker, decider));
		return ret;
	}
	
	/**
	 *  Rank the services of a search with a specific ranker and emit the scores.
	 */
	public static <S> ITerminableIntermediateFuture<Tuple2<S, Double>> rankServicesWithScores(ITerminableIntermediateFuture<S> searchfut, 
		IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		TerminableIntermediateDelegationFuture<Tuple2<S, Double>> ret = new TerminableIntermediateDelegationFuture<Tuple2<S, Double>>();
		searchfut.addResultListener(new ServiceRankingDelegationResultListener2<S>(ret, searchfut, ranker, decider));
		return ret;
	}	
	
	
	// todo: remove these methods, move to marshal service
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getLocalReferenceInfo(Method method, boolean refdef)
	{
		return getReferenceInfo(method, refdef, true);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getRemoteReferenceInfo(Method method, boolean refdef)
	{
		return getReferenceInfo(method, refdef, false);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getReferenceInfo(Method method, boolean refdef, boolean local)
	{
		boolean[] ret;
		Object[] tmp = (Object[])methodreferences.get(method);
		if(tmp!=null)
		{
			ret = (boolean[])tmp[local? 0: 1];
		}
		else
		{
			int params = method.getParameterTypes().length;
			boolean[] localret = new boolean[params];
			boolean[] remoteret = new boolean[params];
			
			for(int i=0; i<params; i++)
			{
				Annotation[][] ann = method.getParameterAnnotations();
				localret[i] = refdef;
				remoteret[i] = refdef;
				for(int j=0; j<ann[i].length; j++)
				{
					if(ann[i][j] instanceof Reference)
					{
						Reference nc = (Reference)ann[i][j];
						localret[i] = nc.local();
						remoteret[i] = nc.remote();
						break;
					}
				}
			}
			
			methodreferences.put(method, new Object[]{localret, remoteret});
			ret = local? localret: remoteret;
		}
		return ret;
	}
	
	/**
	 *  Test if return value is local reference.
	 */
	public static boolean isReturnValueLocalReference(Method method, boolean refdef)
	{
		boolean ret = refdef;
		Reference ref = (Reference)method.getAnnotation(Reference.class);
		if(ref!=null)
			ret = ref.local();
		return ret;
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean isReturnValueRemoteReference(Method method, boolean refdef)
	{
		boolean ret = refdef;
		Reference ref = (Reference)method.getAnnotation(Reference.class);
		if(ref!=null)
			ret = ref.remote();
		return ret;
	}
	
	/**
	 *  Create a required service proxy.
	 */
	protected static <T> T createRequiredProxy(IInternalAccess component, T ser, Class<?> type)
	{
		return (T)BasicServiceInvocationHandler.createRequiredServiceProxy(component, 
			(IService)ser, null, new RequiredServiceInfo(type), null, Starter.isRealtimeTimeout(component.getComponentIdentifier()));
	}
	
	/**
	 *  Create a required service proxies.
	 */
	protected static <T> Collection<T> createRequiredProxies(IInternalAccess component, Collection<T> sers, Class<?> type)
	{
		Collection<T> ret = new ArrayList<T>();
		RequiredServiceInfo reinfo = new RequiredServiceInfo(type);
		reinfo.setMultiple(true);
		if(sers!=null)
		{
			for(T t: sers)
			{
				ret.add((T)BasicServiceInvocationHandler.createRequiredServiceProxy(component, 
					(IService)t, null, reinfo, null, Starter.isRealtimeTimeout(component.getComponentIdentifier())));
			}
		}
		return ret;
	}
	
	/**
	 *  Proxy result listener class.
	 */
	public static class ProxyResultListener<T> extends DelegationResultListener<T>
	{
		protected IInternalAccess component;
		protected Class<?> type;
		
		public ProxyResultListener(Future<T> future, IInternalAccess component, Class<?> type)
		{
			super(future);
			this.component = component;
			this.type = type;
		}
		
		public void customResultAvailable(T result)
		{
			super.customResultAvailable(SServiceProvider.createRequiredProxy(component, result, type));
		}
	}
	
	/**
	 *  Proxy result listener.
	 */
	public static class IntermediateProxyResultListener<T> extends IntermediateDelegationResultListener<T>
	{
		protected IInternalAccess component;
		protected Class<?> type;
		
		public IntermediateProxyResultListener(IntermediateFuture<T> future, IInternalAccess component, Class<?> type)
		{
			super(future);
			this.component = component;
			this.type = type;
		}
		
		public void customResultAvailable(Collection<T> result)
		{
			for(T t: result)
			{
				customIntermediateResultAvailable(t);
			}
			finished();
//			super.customResultAvailable(result);
		}
		
		public void customIntermediateResultAvailable(T result)
		{
			super.customIntermediateResultAvailable(SServiceProvider.createRequiredProxy(component, result, type));
		}
	}
	
	/**
	 *  Check thread access and throw exception if is not component thread.
	 */
	protected static void checkThreadAccess(IInternalAccess component, boolean proxy)
	{
		checkComponent(component);
		if(proxy && !component.getComponentFeature(IExecutionFeature.class).isComponentThread())
			throw new RuntimeException("Must be called on component thread. Use methods with external access otherwise.");
	}
	
	/**
	 *  Check access not null and throw exception otherwise.
	 */
	protected static void checkComponent(IInternalAccess component)
	{
		if(component==null)
			throw new IllegalArgumentException("Access must not be null");
	}
	
	/**
	 *  Check access not null and throw exception otherwise.
	 */
	protected static IFuture<Void> ensureThreadAccess(IInternalAccess component, boolean proxy)
	{
		final Future<Void> ret = new Future<Void>();
		if(component==null)
		{
			ret.setException(new IllegalArgumentException("Access must not be null"));
		}
		else
		{
			if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
			{
				if(proxy)
				{
//					ret.setException(new RuntimeException("Wrong calling thread: "+Thread.currentThread()));
					component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							ret.setResult(null);
							return IFuture.DONE;
						}
					});
				}
				else
				{
					ret.setResult(null);
				}
			}
			else
			{
				ret.setResult(null);
			}
		}
		return ret;
	}
}




