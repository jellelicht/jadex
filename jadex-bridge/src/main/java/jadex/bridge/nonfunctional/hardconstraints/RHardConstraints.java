package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.ComposedRemoteFilter;
import jadex.commons.IRemoteFilter;
import jadex.commons.MethodInfo;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.ExceptionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  Class defining runtime hard constraints.
 *
 */
public class RHardConstraints
{
	/** The basic hard constraints filter */
	protected List<IRemoteFilter<?>> filters = new ArrayList<IRemoteFilter<?>>();
	
	/** Unbound constant value filters */
	protected List<ConstantValueFilter> unboundconstantfilters = new ArrayList<ConstantValueFilter>();
	
	/**
	 *  Creates the runtime hard constraints.
	 * 
	 *  @param mhc The declared model hard constraints.
	 */
	public RHardConstraints(Collection<MHardConstraint> mhc)
	{
		for (MHardConstraint hc : mhc)
		{
			if (MHardConstraint.CONSTANT.equals(hc.getOperator()))
			{
				addFilter(new ConstantValueFilter(hc.getPropertyName(), hc.getValue()));
			}
			else if (MHardConstraint.GREATER.equals(hc.getOperator()))
			{
				addFilter(new StrictInequalityFilter(false));
			}
			else if (MHardConstraint.LESS.equals(hc.getOperator()))
			{
				addFilter(new StrictInequalityFilter(true));
			}
			else if (MHardConstraint.GREATER_OR_EQUAL.equals(hc.getOperator()))
			{
				addFilter(new InequalityFilter(false));
			}
			else if (MHardConstraint.LESS_OR_EQUAL.equals(hc.getOperator()))
			{
				addFilter(new StrictInequalityFilter(true));
			}
			else
			{
				throw new RuntimeException("Unknown hard constraint type: " + hc.getOperator());
			}
		}
	}
	
	/**
	 *  Adds a filter.
	 *  
	 *  @param filter The filter.
	 */
	protected void addFilter(IRemoteFilter<IService> filter)
	{
		if (filter instanceof ConstantValueFilter &&
				((ConstantValueFilter) filter).getValue() == null)
		{
			unboundconstantfilters.add((ConstantValueFilter) filter);
		}
		else
		{
			filters.add(filter);
		}
	}
	
	/**
	 *  Gets the filter that is remotable.
	 * 
	 *  @return Remotable filter.
	 */
	public IRemoteFilter<?> getRemotableFilter()
	{
		IRemoteFilter<?> ret = null;
		
		if (filters.isEmpty())
		{
			ret = IRemoteFilter.ALWAYS;
		} 
		else
		{
			ret = new ComposedRemoteFilter(filters.toArray(new IRemoteFilter[filters.size()]));
		}
		
		return (IRemoteFilter<?>) ret;
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IRemoteFilter<?> getLocalFilter()
	{
		return getLocalFilter(null);
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IRemoteFilter<IService> getLocalFilter(final MethodInfo method)
	{
		IRemoteFilter<IService> ret = null;
		
		if (unboundconstantfilters.isEmpty())
		{
			ret = IRemoteFilter.ALWAYS;
		}
		else
		{
			ret = new IRemoteFilter<IService>()
			{
				public IFuture<Boolean> filter(final IService service)
				{
					final Future<Boolean> filterret = new Future<Boolean>();
					
					final List<ConstantValueFilter> boundconstantfilters = new ArrayList<ConstantValueFilter>();
					
					final CollectionResultListener<Boolean> constantrl = new CollectionResultListener<Boolean>(unboundconstantfilters.size(), false, new IResultListener<Collection<Boolean>>()
					{
						public void resultAvailable(Collection<Boolean> result)
						{
							Boolean[] results = result.toArray(new Boolean[result.size()]);
							boolean filterresult = true;
							for (int i = 0; i < results.length && filterresult; ++i)
							{
								filterresult &= results[i];
							}
							
							if (!filterresult)
							{
								for (ConstantValueFilter bfil : boundconstantfilters)
								{
									bfil.unbind();
								}
							}
							
							filterret.setResult(filterresult);
						};
						
						public void exceptionOccurred(Exception exception)
						{
							resultAvailable(null);
						}
					});
					
					for (int i = 0; i < unboundconstantfilters.size(); ++i)
					{
						final ConstantValueFilter filter = unboundconstantfilters.get(i);
						service.getMethodNFPropertyValue(method, filter.getValueName()).addResultListener(new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
								if (filter.getValue() == null)
								{
									filter.bind(result);
									boundconstantfilters.add(filter);
								}
								filter.filter(service).addResultListener(constantrl);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								constantrl.exceptionOccurred(exception);
							}
						});
					}
					
					return filterret;
				}
			};
		}
		
		return ret;
	}
	
	/**
	 *  Used after searches to make bound filters remotable.
	 */
	public void optimizeFilters()
	{
		List<ConstantValueFilter> newunboundconstantfilters = new ArrayList<ConstantValueFilter>();
		for (ConstantValueFilter fil : unboundconstantfilters)
		{
			if (fil.getValue() != null)
			{
				filters.add(fil);
			}
			else
			{
				newunboundconstantfilters.add(fil);
			}
		}
		unboundconstantfilters = newunboundconstantfilters;
	}
	
	public static <T> ITerminableIntermediateFuture<T> getServices(final IServiceProvider provider, final Class<T> type, final String scope, final MethodInfo method, final RHardConstraints hardconstraints)
	{
		if (hardconstraints == null)
		{
			return SServiceProvider.getServices(provider, type, scope);
		}
		else
		{
			final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
			SServiceProvider.getServices(provider, type, scope, (IRemoteFilter<T>) hardconstraints.getRemotableFilter()).addResultListener(new IResultListener<Collection<T>>()
			{
				public void resultAvailable(Collection<T> results)
				{
					List<T> filteredresults = new ArrayList<T>();
					IRemoteFilter<T> filter = (IRemoteFilter<T>) hardconstraints.getLocalFilter();
					
//					CollectionResultListener<T> crl = new CollectionResultListener<T>(results.size(), true, new DelegationResultListener<T>(new IResultListener<T>()
//					{
//						
//					}));
					
					for (T result : results)
					{
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		return null;
	}
}
