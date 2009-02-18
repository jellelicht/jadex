package jadex.adapter.base.contextservice;

import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatformService;
import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  Contexts are an abstract grouping mechanism for agents on a platform,
 *  which is managed using the context service.
 */
public interface IContextService extends IPlatformService
{
	/**
	 *  Get all contexts on the platform (if any).
	 */
	public IContext[]	getContexts();

	/**
	 *  Get all direct contexts of an agent (if any).
	 */
	public IContext[]	getContexts(IAgentIdentifier agent);
	
	/**
	 *  Get all direct contexts of an agent of a specific type (if any).
	 */
	public IContext[]	getContexts(IAgentIdentifier agent, Class type);
	
	/**
	 *  Get all contexts with a given type (if any).
	 */
	public IContext[]	getContexts(Class type);

	/**
	 *  Get a context with a given name.
	 */
	public IContext	getContext(String name);

	/**
	 *  Create a context.
	 *  @param name	The name or null for auto-generation.
	 *  @param type	The context type.
	 *  @param parent The parent context (if any).
	 *  @param properties Initialization properties (if any).
	 */
	public IContext	createContext(String name, Class type, /*IContext parent,*/ Map properties);
	
	/**
	 *  Delete a context.
	 *  @param listener Listener to be called, when the context is deleted
	 *    (e.g. after all contained agents have been terminated).
	 */
	public void	deleteContext(IContext context, IResultListener listener);
	
	/**
	 *  Register a context factory for a given context type. 
	 */
	public void	addContextFactory(Class type, IContextFactory factory);

	/**
	 *  Deregister a context factory for a given context type. 
	 */
	public void	removeContextFactory(Class type);
}
