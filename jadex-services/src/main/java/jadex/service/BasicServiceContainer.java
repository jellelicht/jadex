package jadex.service;

import jadex.commons.concurrent.IResultListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  A service container is a simple infrastructure for a collection of
 *  services. It allows for starting/shutdowning the container and fetching
 *  service by their type/name.
 *  
 *  The configuration of services is not handled here but can be done
 *  e.g. in subclasses like the PropertyServiceContainer.
 */
public class BasicServiceContainer implements IServiceContainer
{	
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map services;

	/** The platform name. */
	private String name;

	//-------- interface methods --------
		
	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 */
	public Collection getServices(Class type)
	{
		Collection	ret	= null;
		Map tmp = getServiceMap(type);
		if(tmp != null)
			ret	= tmp.values();
		else
			ret	= Collections.EMPTY_SET;
//			throw new RuntimeException("No services found of type: " + type);

		return ret;
	}

	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type, String name)
	{
		Object ret	= null;
		Map tmp = getServiceMap(type);
		if(tmp != null)
		 ret = tmp.get(name);
//		if(ret == null)
//			throw new RuntimeException("Service not found");
		return ret;
	}

	/**
	 *  Get the first declared platform service of a given type.
	 *  @param type The type.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type)
	{
		Object ret	= null;
		Map tmp = getServiceMap(type);
		if(tmp != null && !tmp.isEmpty())
			ret = tmp.values().iterator().next();
//		if(ret == null)
//			throw new RuntimeException("Service not found");
		return ret;
	}
	
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		// Start the services.
		if(services!=null)
		{
			for(Iterator it=services.keySet().iterator(); it.hasNext(); )
			{
				Object key = it.next();
				Map tmp = (Map)services.get(key);
				if(tmp!=null)
				{
					for(Iterator it2=tmp.keySet().iterator(); it2.hasNext(); )
					{
						Object key2 = it2.next();
						IService service = (IService)tmp.get(key2);
						service.startService();
					}
				}
			}
		}
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		// Stop the services.
		if(services!=null)
		{
			for(Iterator it = services.keySet().iterator(); it.hasNext();)
			{
				Object key = it.next();
				Map tmp = (Map)services.get(key);
				if(tmp != null)
				{
					for(Iterator it2 = tmp.keySet().iterator(); it2.hasNext();)
					{
						Object key2 = it2.next();
						IService service = (IService)tmp.get(key2);
	//					System.out.println("Service shutdown: " + service);
						service.shutdownService(null); // Todo: use result listener?
					}
				}
			}
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void addService(Class type, String name, IService service)
	{
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		Map tmp = getServiceMap(type);
		if(tmp == null)
		{
			tmp = new HashMap();
			if(services==null)
				services = new HashMap();
			services.put(type, tmp);
		}
		IService old = (IService)tmp.put(name, service);
		if(old!=null)
			old.shutdownService(null);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, IService service)
	{
		//		System.out.println("Removing service: " + type + " " + service);
		Map tmp = getServiceMap(type);
		if(tmp == null || (service != null && !tmp.containsValue(service)))
			throw new RuntimeException("Service not found: " + service);

		boolean removed = false;
		if(service == null && tmp.size() == 1)
		{
			IService rem = (IService)tmp.remove(tmp.keySet().iterator().next());
			rem.shutdownService(null);
			removed = true;
		}
		else
		{
			for(Iterator it = tmp.keySet().iterator(); it.hasNext();)
			{
				Object key = it.next();
				if(tmp.get(key).equals(service))
				{
					IService rem = (IService)tmp.remove(key);
					rem.shutdownService(null);
					removed = true;
				}
			}
		}

		if(!removed)
			throw new RuntimeException("Service not found: " + service);

		if(tmp.size() == 0)
			services.remove(type);
	}
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 */
	protected Map getServiceMap(Class type)
	{
		return services==null? null: (Map)services.get(type);
	}
}
