package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSIntermediateResultEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Handler for (embedded) sub processes.
 */
public class SubProcessActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
//		System.out.println(instance.getComponentIdentifier().getLocalName()+": sub "+activity);

		MSubProcess	proc	= (MSubProcess)activity;
		final List<MActivity> start = proc.getStartActivities();
		String tmpfile = (String)thread.getPropertyValue("file");
		if(tmpfile == null)
		{
			tmpfile = (String)thread.getPropertyValue("filename");
		}
		final String	file	= tmpfile;
	
		// Internal subprocess (when no file is given and has start activities).
		// Todo: cancel timer on normal/exception exit
		if(start!=null && file==null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
//			thread.setWaiting(true);
			
			boolean wait	= true;
			
			if(MSubProcess.SUBPROCESSTYPE_PARALLEL.equals(proc.getSubprocessType()))
			{
				final String itername = proc.getPropertyValue(MSubProcess.MULTIINSTANCE_ITERATOR).getValue();
				Object val = thread.getParameterValue(itername);
				final Iterator<Object> it = SReflect.getIterator(val);
				
				// If empty parallel activity (i.e. no items at all) continue process.
				if(!it.hasNext())
				{
					wait = false;
				}
				else
				{
					while(it.hasNext())
					{
						Object	value	= it.next();
						for(int i=0; i<start.size(); i++)
						{
							ProcessThread subthread = new ProcessThread((MActivity)start.get(i), thread, instance);
							subthread.setOrCreateParameterValue(itername, value);	// Hack!!! parameter not declared?
							thread.addThread(subthread);
//							System.out.println("val in t: "+subthread+" "+itername+"="+value);
						}
					}
				}
			}
			
			// Todo: support LOOPING in editor.
			else if(MSubProcess.SUBPROCESSTYPE_SEQUENTIAL.equals(proc.getSubprocessType()))// || thread.hasPropertyValue("items"))
			{
//				throw new UnsupportedOperationException("Looping subprocess not yet supported: "+activity+", "+instance);
				final String itername = proc.getPropertyValue(MSubProcess.MULTIINSTANCE_ITERATOR).getValue();
				Object val = thread.getParameterValue(itername);
				final Iterator<Object> it = SReflect.getIterator(val);
				// If empty looping activity (i.e. no items at all) continue process.
				
				IResultCommand<Boolean, Void> cmd = new IResultCommand<Boolean, Void>()
				{
					public Boolean execute(Void args)
					{
						Boolean ret = it.hasNext()? Boolean.TRUE: Boolean.FALSE;
						if(it.hasNext())
						{
							Object elem = it.next();
							for(MActivity st: start)
							{
								ProcessThread subthread = new ProcessThread(st, thread, instance);
								thread.addThread(subthread);
								subthread.setOrCreateParameterValue(itername, elem); // Hack!!! parameter not declared?
							}
						}
						return ret;
					}
				};
				
				if(!cmd.execute(null).booleanValue())
				{
					wait = false;
				}
				else
				{
					thread.setLoopCommand(cmd);
				}
				
//				List<Object> elems = new LinkedList<Object>();
//				while(it.hasNext())
//				{
//					elems.add(0, it.next());
//				}
//				
//				ProcessThread tmp = thread;
//				if(elems.isEmpty())
//				{
//					wait = false;
//				}
//				else
//				{
//					for(Object elem: elems)
//					{
//						ProcessThread cothread = new ProcessThread(null, tmp, instance);
//						cothread.setWaiting(true);
//						tmp.addThread(cothread);
//						tmp = cothread;
//						for(MActivity st: start)
//						{
//							ProcessThread subthread = new ProcessThread(st, cothread, instance);
//							subthread.setWaiting(true);
//							cothread.addThread(subthread);
//							subthread.setParameterValue("item", elem);	// Hack!!! parameter not declared?
//						}
//					}
//					for(ProcessThread t: tmp.getSubthreads())
//					{
//						t.setWaiting(false);
//					}
//				}
				
//				if(!it.hasNext())
//				{
//					wait = false;
//				}
//				else
//				{
//					boolean	first = true;
//					while(it.hasNext())
//					{
//						Object elem = it.next();
//						for(MActivity st: start)
//						{
//							ProcessThread subthread = new ProcessThread(st, thread, instance);
//							thread.addThread(subthread);
//							subthread.setParameterValue("item", elem);	// Hack!!! parameter not declared?
//							if(!first)
//							{
//								subthread.setWaiting(true);
//							}
//						}
//						first = false;
//					}
//				}
			}
			else
			{
				for(int i=0; i<start.size(); i++)
				{
					ProcessThread subthread = new ProcessThread((MActivity)start.get(i), thread, instance);
					thread.addThread(subthread);
				}
			}	
			
			if(wait)
			{
				// todo: support more than one timer?
				MActivity	timer	= null;
				List<MActivity> handlers = activity.getEventHandlers();
				for(int i=0; timer==null && handlers!=null && i<handlers.size(); i++)
				{
					MActivity	handler	= handlers.get(i);
					if(handler.getActivityType().equals("EventIntermediateTimer"))
					{
						timer	= handler;
					}
				}
				
				if(timer!=null)
				{
					instance.getActivityHandler(timer).execute(timer, instance, thread);
				}
				else
				{
					thread.setWaiting(true);
				}
			}
			else
			{
				thread.setNonWaiting();
				instance.step(activity, instance, thread, null);				
			}
		}
		
		// External subprocess
		else if((start==null || start.isEmpty()) && file!=null)
		{
			// Extract arguments from in/inout parameters.
			final Map<String, Object>	args	= new HashMap<String, Object>();
			List<MParameter> params	= activity.getParameters(new String[]{MParameter.DIRECTION_IN, MParameter.DIRECTION_INOUT});
			if(params!=null && !params.isEmpty())
			{
//				args	= new HashMap();
				for(int i=0; i<params.size(); i++)
				{
					MParameter	param	= params.get(i);
					args.put(param.getName(), thread.getParameterValue(param.getName()));
				}
			}
			
//			System.out.println("start: "+instance.getComponentIdentifier()+" "+file);

			thread.setWaiting(true);
			
			instance.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new IResultListener<IComponentManagementService>()
			{
				public void resultAvailable(IComponentManagementService cms)
				{
					// Todo: If remote remember subprocess and kill on cancel.

					final CreationInfo	info = thread.hasPropertyValue("creation info")? 
						(CreationInfo)thread.getPropertyValue("creation info"): new CreationInfo();
					
					// todo: other properties of creation info like
					// instance name and flags like suspend

					if(info.getArguments()==null && args.size()>0)
						info.setArguments(args);
					
					IComponentIdentifier	parent	= thread.hasPropertyValue("parent")
						? (IComponentIdentifier)thread.getPropertyValue("parent")
						: instance.getComponentIdentifier();
					if(info.getParent()==null && parent!=null)
						info.setParent(parent);
					
					String[] imps = instance.getModelElement().getModelInfo().getAllImports();
					if(info.getImports()==null && imps!=null)
						info.setImports(imps);
						
//					System.out.println("parent is: "+parent.getAddresses());	

					cms.createComponent(info, null, file)
						.addResultListener(instance.createResultListener(new IIntermediateResultListener<CMSStatusEvent>()
					{
						public void intermediateResultAvailable(CMSStatusEvent cse)
						{
							if(cse instanceof CMSIntermediateResultEvent)
							{
								String	param	= ((CMSIntermediateResultEvent)cse).getName();
								Object	value	= ((CMSIntermediateResultEvent)cse).getValue();
								
								if(activity.getParameters()!=null && activity.getParameters().get(param)!=null)
								{
									String	dir	= activity.getParameters().get(param).getDirection();
									if(MParameter.DIRECTION_INOUT.equals(dir) || MParameter.DIRECTION_OUT.equals(dir))
									{
										thread.setParameterValue(param, value);
									}
								}
								
								// Todo: event handlers should also react to internal subprocesses???
								List<MActivity> handlers = activity.getEventHandlers();
								
								MActivity handler = null;
								if(handlers!=null)
								{
									for(int i=0; i<handlers.size() && handler==null; i++)
									{
										MActivity act = handlers.get(i);
										
										if(act.isMessageEvent())
										{
											String trig = null;
											if(act.hasProperty(MActivity.RESULTNAME))
											{
												trig = (String)act.getPropertyValueString(MActivity.RESULTNAME);
											}
											if(trig == null || param.equals(trig))
											{
												handler = act;
											}
										}
										else
										{
											String trig = null;
											if(act.hasProperty(MBpmnModel.SIGNAL_EVENT_TRIGGER))
											{
												trig = (String)thread.getPropertyValue(MBpmnModel.SIGNAL_EVENT_TRIGGER, act);
											}
											
											if(act.getActivityType().equals(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL) &&
											   (trig == null || param.equals(trig)))
											{
												handler = act;
											}
										}
									}
								}		

								if(handler!=null)
								{
									ProcessThread newthread	= thread.createCopy();
									updateParameters(newthread);
									
									// todo: allow this, does not work because handler is used for waiting for service calls!
									if(handler.isMessageEvent())
									{
										newthread.setActivity(handler);
										if(handler.hasParameter(MActivity.RETURNPARAM))
										{
											newthread.setParameterValue(MActivity.RETURNPARAM, value);
										}
									}
									else
									{
										newthread.setLastEdge((MSequenceEdge)handler.getOutgoingSequenceEdges().get(0));
									}
									
									thread.getParent().addThread(newthread);
								}
								
//								// Only set result value in thread when out parameter exists.
//								if(activity.getParameters()!=null && activity.getParameters().get(param)!=null)
//								{
//									String	dir	= activity.getParameters().get(param).getDirection();
//									if(MParameter.DIRECTION_INOUT.equals(dir) || MParameter.DIRECTION_OUT.equals(dir))
//									{
//										thread.setParameterValue(param, value);
//
//										// Todo: event handlers should also react to internal subprocesses???
//										List<MActivity> handlers = activity.getEventHandlers();
//										if(handlers!=null)
//										{
//											for(int i=0; i<handlers.size(); i++)
//											{
//												MActivity act = handlers.get(i);
//												String trig = null;
//												if(act.hasProperty(MBpmnModel.SIGNAL_EVENT_TRIGGER))
//												{
//													trig = (String) thread.getPropertyValue(MBpmnModel.SIGNAL_EVENT_TRIGGER, act);
//												}
//												
//												if(act.getActivityType().equals(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL) &&
//												   (trig == null || param.equals(trig)))
//												{
//													ProcessThread newthread	= thread.createCopy();
//													updateParameters(newthread);
//													// todo: allow this, does not work because handler is used for waiting for service calls!
////													newthread.setActivity(act);
//													newthread.setLastEdge((MSequenceEdge)act.getOutgoingSequenceEdges().get(0));
//													thread.getParent().addThread(newthread);
//													
////													ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
////														thread.getId(), instance.getComponentIdentifier(), instance.getComponentDescription().getCreationTime(), instance.createProcessThreadInfo(newthread));
////													instance.notifyListeners(cce);
//													
//													if(instance.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//													{
//														instance.publishEvent(instance.createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread), PublishTarget.TOALL);
//													}
//												}
//											}
//										}									
//									}
//								}								
							}
						}
						
						public void finished()
						{
//							System.out.println("end0: "+instance.getComponentIdentifier()+" "+file+" "+thread.getParameterValue("$results"));
							updateParameters(thread);
							
							thread.setNonWaiting();
							instance.step(activity, instance, thread, null);
						}
						
						public void resultAvailable(Collection<CMSStatusEvent> cses)
						{
							for(CMSStatusEvent cse: cses)
							{
								intermediateResultAvailable(cse);
							}
							finished();
						}
						
						public void exceptionOccurred(final Exception exception)
						{
							// Hack!!! Ignore exception, when component already terminated.
							if(!(exception instanceof ComponentTerminatedException)
								|| !instance.getComponentIdentifier().equals(((ComponentTerminatedException)exception).getComponentIdentifier()))
							{
//								System.out.println("end2: "+instance.getComponentIdentifier()+" "+file+" "+exception);
//								exception.printStackTrace();
								thread.setNonWaiting();
								thread.setException(exception);
								instance.step(activity, instance, thread, null);
							}
						}
						
						protected void updateParameters(ProcessThread thread)
						{
							List<MParameter>	params	= activity.getParameters(new String[]{MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT});
							if(params!=null && !params.isEmpty())
							{
								IValueFetcher fetcher	=null;

								for(int i=0; i<params.size(); i++)
								{
									MParameter	param	= params.get(i);
									if(param.getInitialValue()!=null)
									{
										if(fetcher==null)
											fetcher	= new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
										try
										{
											thread.setParameterValue(param.getName(), ((IParsedExpression) param.getInitialValue().getParsed()).getValue(fetcher));
										}
										catch(RuntimeException e)
										{
											throw new RuntimeException("Error evaluating parameter value: "+instance+", "+activity+", "+param.getName()+", "+param.getInitialValue(), e);
										}
									}
								}
								
								if(activity.getOutgoingDataEdges() != null)	
								{
									for (MDataEdge de : activity.getOutgoingDataEdges())
									{
										thread.setDataEdgeValue(de.getId(), thread.getParameterValue(de.getSourceParameter()));
									}
								}
							}
						}
						
						public String toString()
						{
							return "lis: "+instance.getComponentIdentifier()+" "+file;
						}
					}));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Hack!!! Ignore exception, when component already terminated.
					if(!(exception instanceof ComponentTerminatedException)
						|| !instance.getComponentIdentifier().equals(((ComponentTerminatedException)exception).getComponentIdentifier()))
					{
//						System.out.println("end2: "+instance.getComponentIdentifier()+" "+file+" "+exception);
						exception.printStackTrace();
						thread.setNonWaiting();
						thread.setException(exception);
						instance.step(activity, instance, thread, null);
					}
				}
			});
		}
		
		// Empty subprocess.
		else if((start==null || start.isEmpty()) && file==null)
		{
			// If no activity in sub process, step immediately. 
			instance.step(activity, instance, thread, null);
		}
		
		// Inconsistent subprocess.
		else
		{
			throw new RuntimeException("External subprocess may not have inner activities: "+activity+", "+instance);
		}
	}
}
