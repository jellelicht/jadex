package jadex.bdi.examples.disasterrescue.fireengine;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Int;
import jadex.bdi.examples.disasterrescue.ClearChemicalsTask;
import jadex.bdi.examples.disasterrescue.DisasterType;
import jadex.bdi.examples.disasterrescue.ExtinguishFireTask;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  Move to fires and extinguish them.
 */
public class FireEnginePlan extends Plan
{
	public void body()
	{
		Space2D	space	= (Space2D)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2	home	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
		
		while(true)
		{
			// Find nearest disaster with fire.
			IVector2	mypos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			IVector2	targetpos	= null;
			ISpaceObject	target	= null;
			ISpaceObject[]	disasters	= space.getSpaceObjectsByType("disaster");
			for(int i=0; i<disasters.length; i++)
			{
				if(((Number)disasters[i].getProperty("fire")).intValue()>0 || ((Number)disasters[i].getProperty("chemicals")).intValue()>0)
				{
					IVector2	newpos	= (IVector2)disasters[i].getProperty(Space2D.PROPERTY_POSITION);
					if(target==null || space.getDistance(mypos, newpos).less(space.getDistance(mypos, targetpos)))
					{
						target	= disasters[i];
						targetpos	= newpos;
					}
				}
			}
			
			// Extinguish fire
			if(target!=null)
			{
				// Move to disaster location
				targetpos	= DisasterType.getFireLocation(target);
				IGoal move = createGoal("move");
				move.getParameter("destination").setValue(targetpos);
				dispatchSubgoalAndWait(move);
				
				// Decide between fire and chemicals
				boolean	fire	= ((Number)target.getProperty("fire")).intValue()>0;
				boolean	chemicals	= ((Number)target.getProperty("chemicals")).intValue()>0;
				String	tasktype	= fire && !chemicals ? ExtinguishFireTask.PROPERTY_TYPENAME
					: !fire && chemicals ? ClearChemicalsTask.PROPERTY_TYPENAME
					: fire && chemicals ? Math.random()>0.5 ? ExtinguishFireTask.PROPERTY_TYPENAME : ClearChemicalsTask.PROPERTY_TYPENAME
					: null;
				if(tasktype!=null)
				{
					Map props = new HashMap();
					props.put("disaster", target);
					props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
					Object taskid = space.createObjectTask(tasktype, props, myself.getId());
					SyncResultListener	res	= new SyncResultListener();
					space.addTaskListener(taskid, myself.getId(), res);
					res.waitForResult();
				}
			}
			
			// If no fire and not home: move to home base
			else if(space.getDistance(mypos, home).greater(Vector1Int.ZERO))
			{
				IGoal move = createGoal("move");
				move.getParameter("destination").setValue(home);
				dispatchSubgoalAndWait(move);				
			}
			
			// If no fire and at home: wait a little before checking again
			else
			{
				waitFor((long)(Math.random()*5000));
			}
		}
	}
}
