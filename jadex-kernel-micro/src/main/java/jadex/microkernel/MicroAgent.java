package jadex.microkernel;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.bridge.IMessageService;
import jadex.bridge.IPlatform;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.IResultListener;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Base class for application agents.
 */
public abstract class MicroAgent implements IMicroAgent
{
	//-------- attributes --------
	
	/** The agent interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The current timer. */
	protected ITimer timer;
	
	//-------- constructors --------
	
	/**
	 *  Init the micro agent with the interpreter.
	 *  @param interpreter The interpreter.
	 */
	public void init(MicroAgentInterpreter interpreter)
	{
//		System.out.println("Init: "+interpreter);
		this.interpreter = interpreter;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
	}
	
	/**
	 *  Called when the agent is born and whenever it wants to execute an action
	 *  (e.g. calls wakeup() in one of the other methods).
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 * /
	public boolean executeAction()
	{
		return false;
	}*/
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
	}

	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
	}

	/**
	 *  Called just before the agent is removed from the platform.
	 */
	public void agentKilled()
	{
	}

	//-------- methods --------
	
	/**
	 *  Get the external access for this agent.
	 */
	public IExternalAccess	getExternalAccess()
	{
		return new ExternalAccess(this, interpreter);
	}

	/**
	 *  Get the agent adapter.
	 *  @return The agent adapter.
	 */
	public IComponentAdapter getAgentAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 *  Get the agent platform.
	 *  @return The agent platform. 
	 */
	public IPlatform getPlatform()
	{
		return interpreter.getAgentAdapter().getServiceContainer();
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		return interpreter.getArguments();
	}
	
	/**
	 *  Get an argument.
	 *  @param name The argument name.
	 *  @return The value. 
	 */
	public Object getArgument(String name)
	{
		return interpreter.getArguments().get(name);
	}
	
	/**
	 *  Get the configuration.
	 *  @return the Configuration.
	 */
	public String getConfiguration()
	{
		return interpreter.getConfiguration();
	}
	
	/**
	 *  Create a result listener that is called on the agent thread.
	 *  @param listener The listener to be called on the agent thread.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return interpreter.createResultListener(listener);
	}
	
	/**
	 *  Get the current time.
	 *  @return The current time.
	 */
	public long getTime()
	{
		return ((IClockService)getPlatform().getService(IClockService.class)).getTime();
	}
	
	/**
	 *  Wait for an secified amount of time.
	 *  @param time The time.
	 *  @param run The runnable.
	 */
	public void waitFor(long time, final Runnable run)
	{
		if(timer!=null)
			throw new RuntimeException("timer should be null");
		this.timer = ((IClockService)getPlatform().getService(IClockService.class)).createTimer(time, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				synchronized(interpreter.ext_entries)
				{
					if(!interpreter.ext_forbidden)
					{
						interpreter.invokeLater(new Runnable()
						{
							public void run()
							{
								timer = null;
								run.run();
							}
						});
					}
//					else
//					{
//						System.out.println("not: "+run);
//					}
				}
			}
		});
	}
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	public void waitForTick(final Runnable run)
	{
		if(timer!=null)
			throw new RuntimeException("timer should be null");
		this.timer = ((IClockService)getPlatform().getService(IClockService.class)).createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
//				interpreter.invokeLater(run);
				synchronized(interpreter.ext_entries)
				{
					if(!interpreter.ext_forbidden)
					{
						interpreter.invokeLater(new Runnable()
						{
							public void run()
							{
								timer = null;
								run.run();
							}
						});
					}
//					else
//					{
//						System.out.println("not: "+run);
//					}
				}
			}
		});
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return interpreter.getLogger();
	}	
	
	/**
	 *  Kill the agent.
	 */
	public void killAgent()
	{
		interpreter.getAgentAdapter().killAgent();
	}
		
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public void sendMessage(Map me, MessageType mt)
	{
		((IMessageService)getPlatform().getService(IMessageService.class)).
			sendMessage(me, mt, getAgentIdentifier(), interpreter.getAgentModel().getClassLoader());
	}
	
	/**
	 *  Get the agent name.
	 *  @return The agent name.
	 */
	public String getAgentName()
	{
		return getAgentIdentifier().getLocalName();
	}
	
	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getAgentIdentifier()
	{
		return interpreter.getAgentAdapter().getComponentIdentifier();
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public Map createReply(Map msg, MessageType mt)
	{
		Map reply = new HashMap();
		
		MessageType.ParameterSpecification[] params	= mt.getParameters();
		for(int i=0; i<params.length; i++)
		{
			String sourcename = params[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					reply.put(params[i].getName(), sourceval);
				}
			}
		}
		
		MessageType.ParameterSpecification[] paramsets = mt.getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			String sourcename = paramsets[i].getSource();
			if(sourcename!=null)
			{
				Object sourceval = msg.get(sourcename);
				if(sourceval!=null)
				{
					List tmp = new ArrayList();
					tmp.add(sourceval);
					reply.put(paramsets[i].getName(), tmp);	
				}
			}
		}
		
		return reply;
	}
	
	/**
	 *  Get the application context.
	 *  @return The application context (or null).
	 */
	public IApplicationContext getApplicationContext()
	{
		IApplicationContext ret = null;
		IContextService cs = (IContextService)getPlatform().getService(IContextService.class);
		if(cs!=null)
		{
			IContext[] tmp = cs.getContexts(getAgentIdentifier(), IApplicationContext.class);
			if(tmp.length==1)
				ret = (IApplicationContext)tmp[0];
		}
		return ret;
	}
	
	/**
	 *  Invoke a runnable later that is guaranteed 
	 *  to be executed on agent thread.
	 */
	public void invokeLater(Runnable run)
	{
		interpreter.invokeLater(run);
	}
}
