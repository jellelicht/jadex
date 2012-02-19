package jadex.extension.rs.invoke;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.lang.reflect.Proxy;

/**
 *  Convenience agent that wraps a normal rest web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@ComponentTypes(@ComponentType(name="invocation", filename="jadex/extension/rs/invoke/RestServiceInvocationAgent.class"))
public class RestServiceAgent
{
	//-------- attributes --------
	
	/** The micro agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  Create a wrapper service implementation based on mapping information.
	 */
	public Object createServiceImplementation(Class<?> type, Class<?> impl)
	{
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new RestServiceWrapperInvocationHandler(agent, impl));
	}
}
