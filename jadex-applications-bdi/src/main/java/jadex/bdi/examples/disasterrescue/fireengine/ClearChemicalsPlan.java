package jadex.bdi.examples.disasterrescue.fireengine;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.bdi.examples.disasterrescue.ClearChemicalsTask;
import jadex.bdi.examples.disasterrescue.DisasterType;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  Move to chemicals and clear them.
 */
public class ClearChemicalsPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D	space	= (Space2D)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		ISpaceObject	disaster	= (ISpaceObject)getParameter("disaster").getValue();
		
		// Move to disaster location
		IVector2	targetpos	= DisasterType.getChemicalsLocation(disaster);
		IGoal move = createGoal("move");
		move.getParameter("destination").setValue(targetpos);
		dispatchSubgoalAndWait(move);
		
		// Clear chemicals
		Map props = new HashMap();
		props.put(ClearChemicalsTask.PROPERTY_DISASTER, disaster);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = space.createObjectTask(ClearChemicalsTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener	res	= new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

	/**
	 *  Called when a plan fails.
	 */
	public void failed()
	{
		System.err.println("Plan failed: "+this);
		getException().printStackTrace();
	}
}
