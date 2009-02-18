package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.contextservice.BaseContext;
import jadex.adapter.base.contextservice.IContextFactory;
import jadex.bridge.IPlatform;

import java.util.Map;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ApplicationContextFactory	implements IContextFactory
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform	platform;
	
	//-------- constructors --------
	
	/**
	 *  Create a new default context factory.
	 *  @param platform	The platform.
	 */
	public ApplicationContextFactory(IPlatform platform)
	{
		this.platform	= platform;
	}
	
	//-------- IContextFactory interface --------
	
	/**
	 *  Create a new context.
	 *  @param name	The name of the context.
	 *  @param parent	The parent of the context (if any).
	 *  @param properties	Initialization properties (if any).
	 */
	public BaseContext createContext(String name, /*IContext parent,*/ Map properties)
	{
		return new ApplicationContext(name, /*parent,*/ properties, platform);
	}
}
