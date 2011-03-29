package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
//@ServiceInterface(IAddService.class)
public class PojoAddService implements IAddService
{
	// todo: make injectable these attribues
	
	/** The service identifier. */
//	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The service provider. */
//	@ServiceComponent
	protected IInternalAccess comp;
	
	//-------- methods --------

	/**
	 *  Add two numbers.
	 *  @param a Number one.
	 *  @param b Number two.
	 *  @return The sum of a and b.
	 */
	public IFuture add(double a, double b)
	{
		System.out.println("add service called on: "+sid+", comp="+(comp!=null?comp.getComponentIdentifier():null));
		return new Future(new Double(a+b));
	}
}
