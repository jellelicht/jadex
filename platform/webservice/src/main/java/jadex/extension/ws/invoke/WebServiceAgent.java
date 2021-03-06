package jadex.extension.ws.invoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;

/**
 *  Agent that wraps a normal web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@ComponentTypes(@ComponentType(name="invocation", filename="jadex/platform/service/ws/WebServiceInvocationAgent.class"))
public class WebServiceAgent
{
	//-------- attributes --------
	
	/** The micro agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Create a wrapper service implementation based on the JAXB generated
	 *  Java service class and the service mapping information.
	 */
	public Object createServiceImplementation(Class<?> type, WebServiceMappingInfo mapping)
	{
		return ProxyFactory.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new WebServiceWrapperInvocationHandler(agent, mapping));
	}
}
