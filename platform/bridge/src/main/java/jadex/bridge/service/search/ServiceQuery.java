package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Service query definition. T is the return type for search methods.
 */
public class ServiceQuery<T>
{
	/** Marker for networks not set. */
	protected static final String[]	NETWORKS_NOT_SET	= new String[]{"__NETWORKS_NOT_SET__"};	// TODO: new String[0] for better performance, but unable to check remotely after marshalling!
	
	//-------- attributes --------
	
	/** The service type. */
	protected ClassInfo servicetype;
	
	/** Tags of the service. */
	protected String[] servicetags;
	
	/** The service provider. (rename serviceowner?) */
	protected IComponentIdentifier provider;
	
	/** The service platform. (Find a service from another known platform, e.g. cms) */
	protected IComponentIdentifier platform;
	
	/** The service ID of the target service. Fast lookup of a service by id. */
	protected IServiceIdentifier serviceidentifier;
	
	/** The network names. */
	protected String[] networknames;
	
	/** Should the service be unrestricted. */
	protected Boolean unrestricted;
	
	/** The search scope. */
	protected String scope;
	
	/** The query owner. (rename queryowner?) */
	protected IComponentIdentifier owner;
	
	//-------- influence the result --------
	
	/** Flag, if service by the query owner should be excluded, i.e. do not return my own service. */
	protected boolean excludeowner;
	
	/** The multiple flag. Search for multiple services */
	protected boolean multiple;
	
	/** The return type. Tell registry to return services or service events. */
	protected ClassInfo returntype;
	
	/** The matching mode for multivalued terms. True is and and false is or. */
	protected Map<String, Boolean> matchingmodes;
	
	//-------- identification of a query --------
	
	/** The query id. Id is used for hashcode and equals and the same is used by servicequeryinfo class.
	    Allows for hashing queryies and use a queryinfo object for lookup. */
	protected String id;
	
//	/** Is the query prepared? Prepared means that the query is ready to be processed by the registry. */ 
//	protected boolean prepared;
	
	/**
	 *  Create a new service query.
	 */
	protected ServiceQuery()
	{
		// Not public to not encourage user to use it.
		// Here it does NOT set the networknames automatically because used for serialization.
	}
	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier owner)
//	{
//		this(servicetype, scope, (IFilter<T>) null, null, owner);
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(servicetype, scope, (IFilter<T>) null, provider, owner);
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(Class<T> servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(servicetype, scope, (IFilter<T>) null, provider, owner);
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(Class<T> servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(new ClassInfo(servicetype), scope, filter, provider, owner);
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(Class<T> servicetype, String scope, IFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
////		this(new ClassInfo(servicetype), scope, filter, provider, owner);this.returntype = servicetype;
//		this.servicetype = new ClassInfo(servicetype);
//		this.returntype = this.servicetype;
//		// todo: what is the best place for this?
//		this.scope = scope==null && ServiceIdentifier.isSystemService(servicetype)? RequiredServiceInfo.SCOPE_PLATFORM: scope;
//		this.filter = filter;
//		this.provider = provider;
//		this.owner = owner;
//		
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this.returntype = servicetype;
//		this.servicetype = servicetype;
//		this.scope = scope;
//		this.filter = filter;
//		this.provider = provider;
//		this.owner = owner;
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(Class<T> returntype, Class<?> servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(new ClassInfo(returntype), new ClassInfo(servicetype), scope, filter, provider, owner);
//	}
	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo returntype, ClassInfo servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this.returntype = returntype;
//		this.servicetype = servicetype;
//		this.scope = scope;
//		this.filter = filter;
//		this.provider = provider;
//		this.owner = owner;
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(servicetype, servicetype, scope, filter, provider, owner);
////		this.returntype = servicetype;
////		this.servicetype = servicetype;
////		this.scope = scope;
////		this.filter = filter;
////		this.provider = provider;
////		this.owner = owner;
//	}
	
	/**
	 *  Create a new service query.
	 *  owner = startpoint
	 */
	public ServiceQuery(Class<?> servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner)
	{
		this(servicetype, scope, provider, owner, null);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(Class<?> servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner, Class<?> returntype)
	{
		this(servicetype!=null? new ClassInfo(servicetype): null, scope==null && ServiceIdentifier.isSystemService(servicetype)? RequiredServiceInfo.SCOPE_PLATFORM: scope,
			provider, owner, returntype!=null? new ClassInfo(returntype): null);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner)
	{
		this(servicetype, scope, provider, owner, servicetype);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner, ClassInfo returntype)
	{
//		if(owner==null)
//			throw new IllegalArgumentException("Owner must not null");
		
		this.servicetype = servicetype;
		this.scope = scope;
		this.provider = provider;
		this.owner = owner;
		this.returntype = returntype;
		
		this.id = SUtil.createUniqueId();
		this.networknames = NETWORKS_NOT_SET;
	}
	
	/**
	 *  Shallow copy constructor.
	 *  @param original Original query.
	 */
	public ServiceQuery(ServiceQuery<T> original)
	{
		this.returntype = original.returntype;
		this.servicetype = original.servicetype;
		this.scope = original.scope;
		this.servicetags = original.servicetags;
		this.owner = original.owner;
		this.id = original.id;
		this.networknames = original.networknames;
		this.matchingmodes = original.matchingmodes;
		this.platform	= original.platform;
		this.provider	= original.provider;
		this.unrestricted = original.unrestricted;
	}

	/**
	 *  Get the service type.
	 *  @return The service type
	 */
	public ClassInfo getServiceType()
	{
		return servicetype;
	}

	/**
	 *  Set the service type.
	 *  @param type The service type to set
	 */
	public void setServiceType(ClassInfo servicetype)
	{
		this.servicetype = servicetype;
	}
	
	/**
	 *  Get the return type.
	 *  @return The return type
	 */
	public ClassInfo getReturnType()
	{
		return returntype;
	}

	/**
	 *  Set the return type.
	 *  @param type The return type to set
	 */
	public void setReturnType(ClassInfo returntype)
	{
		this.returntype = returntype;
	}
	
	/**
	 *  Get the scope.
	 *  @return The scope
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}
	
	/**
	 *  Gets the service tags.
	 *  
	 *  @return The service tags. 
	 */
	public String[] getServiceTags()
	{
		return servicetags;
	}
	
	/**
	 *  Sets the service tags.
	 *  @param servicetags The service tags. 
	 */
	public void setServiceTags(String[] servicetags)
	{
		TagProperty.checkReservedTags(servicetags);
		this.servicetags = servicetags;
	}
	
	/**
	 *  Sets the service tags.
	 *  @param servicetags The service tags.
	 *  
	 *  todo: move or refactor to hide complexity!?
	 */
	public void setServiceTags(String[] servicetags, IExternalAccess component)
	{
		this.servicetags = TagProperty.createRuntimeTags(servicetags, component).toArray(new String[servicetags!=null ? servicetags.length : 0]);
	}
	
	/**
	 *  Get the provider.
	 *  @return The provider
	 */
	public IComponentIdentifier getProvider()
	{
		return provider;
	}

	/**
	 *  Set the provider.
	 *  @param provider The provider to set
	 */
	public void setProvider(IComponentIdentifier provider)
	{
		this.provider = provider;
	}
	
	/**
	 *  Get the platform.
	 *  @return The platform
	 */
	public IComponentIdentifier getPlatform()
	{
		return platform;
	}
	
	/**
	 *  Set the platform.
	 *  @param platform The platform
	 */
	public void setPlatform(IComponentIdentifier platform)
	{
		this.platform = platform;
	}
	
	/**
	 *  Gets the service identifier.
	 *
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return serviceidentifier;
	}

	/**
	 *  Sets the service identifier.
	 *
	 *  @param serviceidentifier The service identifier.
	 */
	public void setServiceIdentifier(IServiceIdentifier serviceidentifier)
	{
		this.serviceidentifier = serviceidentifier;
	}

	/**
	 *  Get the owner.
	 *  @return The owner
	 */
	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	/**
	 *  Set the owner.
	 *  @param owner The owner to set
	 */
	public void setOwner(IComponentIdentifier owner)
	{
		this.owner = owner;
	}
	
	/**
	 *  Checks if service of the query owner should be excluded.
	 *  
	 *  @return True, if the services should be excluded.
	 */
	public boolean isExcludeOwner()
	{
		return excludeowner;
	}
	
	/**
	 *  Sets if service of the query owner should be excluded.
	 *  
	 *  @param excludeowner True, if the services should be excluded.
	 */
	public void setExcludeOwner(boolean excludeowner)
	{
		this.excludeowner = excludeowner;
	}
	
	/**
	 *  Get the id.
	 *  @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the multiple.
	 *  @return the multiple
	 */
	public boolean isMultiple()
	{
		return multiple;
	}

	/**
	 *  Set the multiple.
	 *  @param multiple The multiple to set
	 */
	public void setMultiple(boolean multiple)
	{
		this.multiple = multiple;
	}

	/**
	 *  Gets the specification for the indexer.
	 *  Query needs to be enhanced before calling this method. See ServiceRegistry.enhanceQuery()
	 *  
	 *  @return The specification for the indexer.
	 */
	public List<Tuple3<String, String[], Boolean>> getIndexerSearchSpec()
	{
		List<Tuple3<String, String[], Boolean>> ret = new ArrayList<Tuple3<String,String[],Boolean>>();
		
		if(platform != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_PLATFORM, new String[]{platform.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_PLATFORM)));
		
		if(provider != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_PROVIDER, new String[]{provider.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_PROVIDER)));
		
		if(servicetype != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_INTERFACE, new String[]{servicetype.getGenericTypeName()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_INTERFACE)));
		
		if(servicetags != null && servicetags.length > 0)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_TAGS, servicetags, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_TAGS)));
		
		if(serviceidentifier != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_SID, new String[]{serviceidentifier.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_SID)));
		
		assert !Arrays.equals(networknames, NETWORKS_NOT_SET) : "Problem: query not enhanced before processing.";
		
		if(networknames != null && networknames.length>0)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_NETWORKS, networknames, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_NETWORKS)));
		
		return ret;
	}
	
	
		
	/**
	 *  Get the matching mode for a key.
	 *  @param key The key name.
	 *  @return True for and, false for or.
	 */
	public Boolean getMatchingMode(String key)
	{
		return matchingmodes!=null? matchingmodes.get(key): null;
	}
	
	/**
	 *  Set a matching mode.
	 *  @param key The key name.
	 *  @param and True for and.
	 */
	public void setMatchingMode(String key, Boolean and)
	{
		if(matchingmodes==null)
			matchingmodes = new HashMap<String, Boolean>();
		matchingmodes.put(key, and);
	}
	
	
	/**
	 *  Get the unrestricted mode.
	 *  @return The unrestricted mode.
	 */
	public Boolean isUnrestricted()
	{
		return unrestricted;
	}

	/**
	 *  Set the unrestricted mode.
	 *  @param unrestricted the unrestricted to set
	 */
	public void setUnrestricted(Boolean unrestricted)
	{
		this.unrestricted = unrestricted;
	}

	/**
	 *  Check if a query is potentially remote.
	 *  @return True, if scope is set to a remote scope (e.g. global or network).
	 */
	public boolean isRemote()
	{
		return scope!=null && (scope.contains("global") || scope.contains("network"));
	}
	
	/**
	 *  Tests if the query keys matches a service.
	 *  
	 *  @param service The service.
	 *  @return True, if the service matches the keys.
	 */
	protected boolean matchesKeys(IServiceIdentifier service)
	{
		if (servicetype != null && !service.getServiceType().getGenericTypeName().equals(servicetype))
			return false;
		
		if (servicetags != null)
		{
			Set<String> tagsset = ServiceKeyExtractor.getKeysStatic(ServiceKeyExtractor.KEY_TYPE_TAGS, service);
			if (tagsset == null)
				return false;
			
			for (String tag : servicetags)
			{
				if (!tagsset.contains(tag))
				{
					return false;
				}
			}
		}
		
		if (provider != null && !provider.equals(service.getProviderId()))
			return false;
		
		if (platform != null && !platform.equals(service.getProviderId().getRoot()))
			return false;
		
		return true;
	}
	
//	/**
//	 *  Get the prepared.
//	 *  @return the prepared
//	 */
//	public boolean isPrepared()
//	{
//		return prepared;
//	}
//
//	/**
//	 *  Set the prepared.
//	 *  @param prepared The prepared to set
//	 */
//	public void setPrepared(boolean prepared)
//	{
//		this.prepared = prepared;
//	}
	
//	/**
//	 *  Prepare the query.
//	 */
//	public void prepare(IComponentIdentifier cid)
//	{
//		if(!prepared)
//		{
//			networknames = getNetworkNames(cid);
//			prepared = true;
//		}
//	}
	
	
	
	/**
	 *  Static helper method to get the current network names.
	 *  @param cid The platform cid.
	 *  @return The current network names.
	 */
	public static String[] getNetworkNames(IComponentIdentifier cid)
	{
		if(cid==null)
			return SUtil.EMPTY_STRING_ARRAY;
		
		Set<String> nnames = (Set<String>)Starter.getPlatformValue(cid, Starter.DATA_NETWORKNAMESCACHE);
		return nnames!=null? nnames.toArray(new String[0]): SUtil.EMPTY_STRING_ARRAY;
	}

	/**
	 *  Get the networknames.
	 *  @return the networknames
	 */
	public String[] getNetworkNames()
	{
		return networknames;
	}

	/**
	 *  Set the networknames.
	 *  @param networknames The networknames to set
	 */
	public void setNetworkNames(String... networknames)
	{
		this.networknames = networknames;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return id.hashCode()*13;
	}

	/**
	 *  Test if other object equals this one.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof ServiceQuery)
		{
			ServiceQuery<?> other = (ServiceQuery<?>)obj;
			ret = SUtil.equals(getId(), other.getId());
		}
		return ret;
	}
	
	/**
	 *  Get the target platform if specified (using platform and provider).
	 *  @return The target platform.
	 */
	public IComponentIdentifier getTargetPlatform()
	{
		return getPlatform()!=null? getPlatform().getRoot(): getProvider()!=null? getProvider().getRoot(): null;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ServiceQuery(servicetype=" + servicetype + ", servicetags=" + Arrays.toString(servicetags) + ", provider=" + provider + ", platform=" + platform 
			+ ", networknames=" + Arrays.toString(networknames) + ", unrestricted=" + unrestricted + ", scope=" + scope + ", owner=" + owner
			+ ", id=" + id + ")";
	}
}
