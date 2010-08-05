package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;

import java.util.Map;

/**
 *  Basic service provide a simple default isValid() implementation
 *  that returns true after start service and false afterwards.
 */
public class BasicService implements IService
{
	//-------- attributes --------

	/** The id counter. */
	protected static long idcnt;
	
	/** The valid state. */
	protected boolean valid;
	
	/** The service id. */
	protected IServiceIdentifier sid;
	
	/** The service properties. */
	protected Map properties;
	
	//-------- constructors --------

	/**
	 *  Create a new service.
	 * /
	public BasicService()
	{
		this(null);
	}*/
	
	/**
	 *  Create a new service.
	 * /
	public BasicService(IServiceIdentifier sid)
	{
		this.sid = sid;
	}*/
	
	/**
	 *  Create a new service.
	 */
	public BasicService(Object providerid, Class type, Map properties)
	{
		this.sid = createServiceIdentifier(providerid, type, getClass());
		this.properties	= properties;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public boolean isValid()
	{
//		return true;
		return valid;
	}
	
	/**
	 *  Get the service id.
	 *  @return The service id.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService()
	{
//		System.out.println("start: "+this);
		Future ret = new Future();
		if(isValid())
		{
			ret.setException(new RuntimeException("Already running."));
		}
		else
		{
			valid = true;
			ret.setResult(this);
		}
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture	shutdownService()
	{
		Future ret = new Future();
		if(!isValid())
		{
			ret.setException(new RuntimeException("Not running."));
		}
		else
		{
			valid = false;
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Generate a unique name.
	 *  @param The calling service class.
	 */
	private static String generateServiceName(Class service)
	{
		synchronized(BasicService.class)
		{
			return service.getName()+"_#"+idcnt++;
		}
	}
	
	/**
	 *  Create a new service identifier.
	 *  @param providerid The provider id.
	 *  @param servicename The service name.
	 *  @return A service identifier.
	 */
	private static IServiceIdentifier createServiceIdentifier(Object providerid, Class servicetype, Class serviceimpl)
	{
		return new ServiceIdentifier(providerid, servicetype, generateServiceName(serviceimpl));
	}
}
