package jadex.simulation.examples.marsworld.carry;


import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.bridge.IExternalAccess;
import jadex.extension.agr.AGRSpace;
import jadex.extension.agr.Group;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

/**
 *  Inform the sentry agent about a new target.
 */
public class NewInformNewTargetPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IChangeEvent	reason	= (IChangeEvent)getReason();
		ISpaceObject	target	= (ISpaceObject)reason.getValue();
				
		AGRSpace agrs = (AGRSpace)((IExternalAccess)getScope().getParent()).getExtension("myagrspace").get(this);
		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	sentries	= group.getAgentsForRole("sentry");
		
		//send only to one sentry
//		IComponentIdentifier randomSentry = sentries[GetRandom.getRandom(sentries.length)];
//		System.out.println("Sending target from:  " + getComponentName() + " to " + randomSentry.getLocalName());
		
		//to closest sentry
		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParent()).getExtension("my2dspace").get(this);
		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
		ISpaceObject nearestSentry = space.getNearestObject(myPos, null, "sentry");
//		ISpaceObject[] tmp = space.getSpaceObjectsByType("sentry");
//		
//		System.out.println("All sentries with pos from: " + getComponentName());
//		for(ISpaceObject o : tmp){
//			System.out.println(o.getId() + " : " +  o.getProperty("position") + " -> Distance: " + space.getDistance(myPos,  (IVector2) o.getProperty("position")));
//		}
//		System.out.println("Nearest sentry with pos from: " + getComponentName() + " : " + nearestSentry.getId()  + " - Owner: " + space.getOwner(nearestSentry.getId()).getLocalName());
				
		
		if(space.getOwner(nearestSentry.getId()).getName()!=null)
		{
			IMessageEvent mevent = createMessageEvent("inform_target");
			mevent.getParameterSet(SFipa.RECEIVERS).addValue(space.getOwner(nearestSentry.getId()).getName());
			mevent.getParameter(SFipa.CONTENT).setValue(target);
			sendMessage(mevent);
		}

//		System.out.println("Informing sentries: "+getScope().getAgentName());
	}

}
