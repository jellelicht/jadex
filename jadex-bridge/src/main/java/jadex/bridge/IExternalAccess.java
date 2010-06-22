package jadex.bridge;

import jadex.commons.IFuture;
import jadex.service.IServiceProvider;

/**
 *  The interface for accessing components from the outside.
 *  To be specialized for concrete component types.
 */
public interface IExternalAccess extends IServiceProvider
{
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
//	public ILoadableComponentModel	getModel();
	public IFuture	getModel();

	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
//	public IComponentIdentifier	getComponentIdentifier();
	public IFuture getComponentIdentifier();
	
	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
//	public IExternalAccess getParent();
	public IFuture getParent();
	
//	/**
//	 *  Get service.
//	 *  @return The service implementation.
//	 */
//	public Object getService(Class type);
	
	/**
	 *  Get service interfaces.
	 *  @return Array of provided services.
	 * /
	public Class[] getServiceInterfaces();*/
	
	// todo:?!
//	/**
//	 *  Get the children (if any).
//	 *  @return The children.
//	 */
//	public IComponentIdentifier[] getChildren();
	
}
