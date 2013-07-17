package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.garbagecollector.GarbageCollectorBDI.Pick;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;

import java.util.HashMap;
import java.util.Map;

/**
 *  Try to pickup some piece of garbage.
 */
@Plan
public class PickUpPlanEnv
{
	//-------- attributes --------

	@PlanCapability
	protected GarbageCollectorBDI collector;
		
	@PlanAPI
	protected IPlan rplan;
		
	@PlanReason
	protected Pick goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
//		System.out.println("Pickup plan: "+getAgentName()+" "+getReason());
		
		IEnvironmentSpace env = collector.getEnvironment();
		// todo: garbage as parameter?
		
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, collector.getAgent().getComponentDescription());
		env.performSpaceAction("pickup", params, lis); // todo: garbage as parameter?
		fut.get();  
		
		// todo: handle result
//		if(!((Boolean)srl.waitForResult()).booleanValue()) 
//			fail();
		
//		System.out.println("pickup plan end");
	}
}
