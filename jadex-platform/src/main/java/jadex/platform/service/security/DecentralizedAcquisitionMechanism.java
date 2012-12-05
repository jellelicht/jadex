package jadex.platform.service.security;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.bridge.service.types.security.ParameterInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *  The decentralized acquisition mechanism is based on a peer to peer scheme. It
 *  asks all available platforms for the certificate of a specific caller.
 *  It then waits for the first n (acquirecnt) answers and checks if it received
 *  consistent results (all are equal). If this is the case it will deliver the
 *  certificate, otherwise an exception is raised. 
 */
public class DecentralizedAcquisitionMechanism extends AAcquisitionMechanism
{
	//-------- attributes --------
	
	/** The aquirecnt (<1=disabled). */
	protected int aquirecnt;
	
	//-------- constructors --------

	/**
	 *  Create a new mechanism.
	 */
	public DecentralizedAcquisitionMechanism()
	{
		this(1);
	}
	
	/**
	 *  Create a new mechanism.
	 */
	public DecentralizedAcquisitionMechanism(int aquirecnt)
	{
		this.aquirecnt = aquirecnt;
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public IFuture<Certificate> acquireCertificate(final String name)
	{
		final Future<Certificate> ret = new Future<Certificate>();

		if(aquirecnt<1)
		{
			ret.setException(new SecurityException("Certificate not available and aquisition disabled: "+name));
			return ret;
		}
		
		final IComponentIdentifier cid = new ComponentIdentifier(name);
		
		// Try to fetch certificate from other platforms
		SServiceProvider.getServices(secser.getComponent().getServiceContainer(), ISecurityService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new IIntermediateResultListener<ISecurityService>()
		{
			protected int ongoing;
			
			protected boolean finished;
			
			protected List<Certificate> certs = new ArrayList<Certificate>();
			
			public void intermediateResultAvailable(ISecurityService ss)
			{
				ongoing++;
				
				if(!((IService)ss).getServiceIdentifier().equals(secser.getSid()))
				{
					ss.getPlatformCertificate(cid).addResultListener(new IResultListener<Certificate>()
					{
						public void resultAvailable(Certificate cert)
						{
							certs.add(cert);
							if(certs.size()>=aquirecnt && !ret.isDone())
							{
								try
								{
									byte[] enc = certs.get(0).getEncoded();
									boolean ok = true;
									for(int i=1; i<certs.size() && ok; i++)
									{
										if(!Arrays.equals(enc, certs.get(i).getEncoded()))
										{
											ret.setException(new SecurityException("Received different certificates for: "+name));
											ok = false;
										}
									}
									if(ok)
									{
										ret.setResult(certs.get(0));
									}
								}
								catch(Exception e)
								{
									ret.setException(new SecurityException("Certificate encoding error: "+name));
								}
							}
							ongoing--;
							checkFinish();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// ignore failures of getCertificate calls
							ongoing--;
							checkFinish();
						}
					});
				}
			}
			
			public void finished()
			{
				finished = true;
				checkFinish();
			}
			
			public void resultAvailable(Collection<ISecurityService> result)
			{
				for(ISecurityService ss: result)
				{
					intermediateResultAvailable(ss);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				finished();
			}
			
			protected void checkFinish()
			{
				if(ongoing==0 && finished && !ret.isDone())
				{
					ret.setExceptionIfUndone(new SecurityException("Unable to retrieve certificate: "+name));
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the mechanism info for the gui.
	 */
	public MechanismInfo getMechanismInfo()
	{
		ParameterInfo pi = new ParameterInfo("aquirecnt", int.class, new Integer(aquirecnt));
		List<ParameterInfo> params = new ArrayList<ParameterInfo>();
		params.add(pi);
		MechanismInfo ret = new MechanismInfo("Decentralized", getClass(), params);
		return ret;
	}
	
	/**
	 *  Set a mechanism parameter value.
	 */
	public void setParameterValue(String name, Object value)
	{
		System.out.println("set param val: "+name+" "+value);
		
		if("aquirecnt".equals(name))
		{
			aquirecnt = ((Integer)value).intValue();
		}
		else
		{
			throw new RuntimeException("Unknown parameter: "+name);
		}
	}
}
