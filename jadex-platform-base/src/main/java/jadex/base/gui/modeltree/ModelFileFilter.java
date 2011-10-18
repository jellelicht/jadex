package jadex.base.gui.modeltree;

import jadex.base.SComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.LibraryService;
import jadex.commons.IRemoteFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.util.List;

/**
 *  Filter for Jadex component models. 
 *  
 *  Is so complicated because it has to work locally and must be transferable.
 */
public class ModelFileFilter implements IRemoteFilter
{
	//-------- attributes --------
	
	/** The all selected flag. */
	protected boolean all;
	
	/** The list of selected component names. */
	protected List selectedcomponents;
	
	/** The menu item constructor. */
	protected ModelFileFilterMenuItemConstructor filtercon;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	//-------- constructors --------
	
	/**
	 *  Create a new filter.
	 */
	public ModelFileFilter()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new filter.
	 */
	public ModelFileFilter(ModelFileFilterMenuItemConstructor filtercon, IExternalAccess exta)
	{
		this.filtercon = filtercon;
		this.exta = exta;
	}

	//-------- methods --------

	/**
	 *  Set the all.
	 *  @param all The all to set.
	 */
	public void setAll(boolean all)
	{
		this.all = all;
	}
	
	/**
	 *  Get the all.
	 *  @return the all.
	 */
	public boolean isAll()
	{
		boolean ret;
		if(filtercon!=null)
			ret = filtercon.isAll();
		else
			ret = all;
		return ret;
	}

	/**
	 *  Get the list of selected components.
	 *  @return The list of components.
	 */
	public List getSelectedComponents()
	{
		List ret;
		if(filtercon!=null)
			ret = filtercon.getSelectedComponentTypes();
		else
			ret = selectedcomponents;
		return ret;
	}

	/**
	 *  Set the list of selected components.
	 *  @param selectedcomponents The list of selcted components.
	 */
	public void setSelectedComponents(List selectedcomponents)
	{
		this.selectedcomponents = selectedcomponents;
	}
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return exta;
	}

	/**
	 *  Set the external access.
	 *  @param exta The external acccess.
	 */
	public void setExternalAccess(IExternalAccess exta)
	{
		this.exta = exta;
	}

	/**
	 *  Test if object is accepted by filter.
	 *  @param obj The object to filter.
	 *  @return True, if ok.
	 */
	public IFuture<Boolean> filter(Object obj)
	{
		final Future<Boolean> ret =  new Future<Boolean>();
		
		if(obj instanceof File)
		{
			final File file = (File)obj;
			if(isAll() || file.isDirectory())
			{
				ret.setResult(Boolean.TRUE);
			}
			else
			{
				SServiceProvider.getServiceUpwards(exta.getServiceProvider(), ILibraryService.class)
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Boolean>(ret)
				{
					public void customResultAvailable(ILibraryService libservice)
					{
						libservice.getResourceIdentifier(SUtil.toURL(file.getAbsolutePath())).addResultListener(
							new ExceptionDelegationResultListener<IResourceIdentifier, Boolean>(ret)
						{
							public void customResultAvailable(IResourceIdentifier rid)
							{
//								IResourceIdentifier rid = libservice.getResourceIdentifier(file.getAbsolutePath());
								SComponentFactory.isModelType(exta, file.getAbsolutePath(), getSelectedComponents(), rid)
									.addResultListener(new DelegationResultListener<Boolean>(ret));
							}
						});
						
					}
				});
			}
		}
		else
		{
			ret.setResult(Boolean.FALSE);
		}
		return ret;
	}
}