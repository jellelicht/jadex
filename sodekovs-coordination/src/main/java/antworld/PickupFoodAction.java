package antworld;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.Collection;
import java.util.Map;

/**
 *  Action for picking up food.
 */
public class PickupFoodAction extends SimplePropertyObject implements ISpaceAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{	
		boolean ret = false;
				
		Grid2D grid = (Grid2D)space;
		
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getAvatar(owner);

	
		assert so.getProperty("food")==null: so;
		
		//TODO: atomic action?
		Collection food = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.PROPERTY_POSITION), "food");
		ISpaceObject pickedFood = (ISpaceObject)(food!=null? food.iterator().next(): null);		
		
//		System.out.println("#"+ so.getId() + "#pickup food action: "+so+" "+so.getProperty(Grid2D.PROPERTY_POSITION)+" "+pickedFood);
		if(food!=null)
		{
//			if(Math.random()>0.88)
//			{
				Collection foodSources = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.PROPERTY_POSITION), "foodSource");
				ISpaceObject foodSource = (ISpaceObject) foodSources.iterator().next();
				int stock = ((Integer)foodSource.getProperty("stock")).intValue();
				foodSource.setProperty("stock", new Integer(stock-1));
				
				food.remove(pickedFood);
//				System.out.println("pickup: "+waste);
				so.setProperty("food", pickedFood);
				
				//Support evaluation
				int carriedFood = ((Integer)so.getProperty("eval:carriedFood")).intValue();
				so.setProperty("eval:carriedFood", new Integer(carriedFood+1));				
				
				grid.setPosition(pickedFood.getId(), null);
				ret = true;
				//pcs.firePropertyChange("worldObjects", garb, null);
//				System.out.println("Agent picked up: "+owner+" "+so.getProperty(Space2D.POSITION));
//			}
//			else
//			{
//				System.out.println("#PickUpFoodAction#Agent Pick UP failed randomly.");
//			}
		}
		else
		{
			System.out.println("#"+ so.getId() +  " - PickupFoodAction#Agent picked up failed.");
		}

//		System.out.println("pickup food action "+parameters + "completed.");

		return ret? Boolean.TRUE: Boolean.FALSE;
	}
}
