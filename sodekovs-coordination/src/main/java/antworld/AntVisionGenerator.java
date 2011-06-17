package antworld;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.EnvironmentEvent;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.IPerceptGenerator;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.List;

/**
 * Percept generator for ant agents.
 */
public class AntVisionGenerator extends SimplePropertyObject implements IPerceptGenerator {
	// -------- constants --------

	// /** Constant for garabge appeared. */
	// public static final String GARBAGE_APPEARED = "garbage_appeared";
	//
	// /** Constant for garabge disappeared. */
	// public static final String GARBAGE_DISAPPEARED = "garbage_disappeared";

	/** Constant for gravitation felt. */
	public static final String GRAVITATION_FELT = "gravitation_felt";

	// -------- attributes --------

	/** The consuming agents. */
	protected List agents;

	// -------- IPerceptGenerator --------

	/**
	 * Called when an agent was added to the space.
	 * 
	 * @param agent
	 *            The agent identifier.
	 * @param space
	 *            The space.
	 */
	public void agentAdded(IComponentDescription agent, IEnvironmentSpace space) {
		// Only add agents of type "Ant"
		if ("Ant".equals(agent.getType())) {
			if (agents == null)
				agents = new ArrayList();
			agents.add(agent);
		}
	}

	/**
	 * Called when an agent was remove from the space.
	 * 
	 * @param agent
	 *            The agent identifier.
	 * @param space
	 *            The space.
	 */
	public void agentRemoved(IComponentDescription agent, IEnvironmentSpace space) {
		agents.remove(agent);
		if (agents.size() == 0)
			agents = null;
	}

	// -------- IEnvironmentListener --------

	/**
	 * Test if an event is relevant for the percept generator.
	 * 
	 * @param event
	 *            The event. / public boolean isRelevant(EnvironmentEvent event)
	 *            { return agents!=null &&
	 *            "garbage".equals(event.getSpaceObject().getType()) &&
	 *            EnvironmentEvent
	 *            .OBJECT_POSITION_CHANGED.equals(event.getType()); }
	 */

	/**
	 * Dispatch an environment event to this listener.
	 * 
	 * @param event
	 *            The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event) {

		// System.out.println("EventType: " + event.getType() + ", object: " +
		// event.getSpaceObject().getType());
		if (agents != null && "ant".equals(event.getSpaceObject().getType())) {
			for (int i = 0; i < agents.size(); i++) {
				IComponentDescription agent = (IComponentDescription) agents.get(i);				
				if(EnvironmentEvent.OBJECT_PROPERTY_CHANGED.equals(event.getType()) && Space2D.PROPERTY_POSITION.equals(event.getProperty())) {
					IVector2 pos = (IVector2) event.getSpaceObject().getProperty(Space2D.PROPERTY_POSITION);
					// IVector2 oldpos = (IVector2) event.getInfo();
					ISpaceObject agentobj = event.getSpace().getAvatar(agent);

					if (agentobj.getProperty(Space2D.PROPERTY_POSITION).equals(pos)) {
						IVector2 gravitationCenter = checkForGravitation(event.getSpace(), pos);
						// percept felt gravitation
						if (gravitationCenter != null) {
							System.out.println("GRAVITATION Percept created for: " + agent.toString() + ", event: " + event.getSpaceObject().toString());
							((AbstractEnvironmentSpace) event.getSpace()).createPercept(GRAVITATION_FELT, gravitationCenter, agent, agentobj);
						}
						// } else if
						// (agentobj.getProperty(Space2D.POSITION).equals(oldpos))
						// {
						// // percept garbage disappeared
						// // perproc.processPercept(event.getSpace(),
						// // GARBAGE_DISAPPEARED, event.getSpaceObject(),
						// agent);
						// ((AbstractEnvironmentSpace)
						// event.getSpace()).createPercept(GARBAGE_DISAPPEARED,
						// event.getSpaceObject(), agent);
						// }
					}
					// else if
					// (EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
					// {
					// // percept garbage disappeared
					// // perproc.processPercept(event.getSpace(),
					// // GARBAGE_DISAPPEARED, event.getSpaceObject(), agent);
					// ((AbstractEnvironmentSpace)
					// event.getSpace()).createPercept(GARBAGE_DISAPPEARED,
					// event.getSpaceObject(), agent);
					// }
				}
			}
		}
	}

	/**
	 * Checks whether the position has gravitation. If yes, then the position of
	 * the gravitation center is returned. Otherwise null.
	 * 
	 * @param space
	 * @param pos
	 * @return
	 */
	private IVector2 checkForGravitation(IEnvironmentSpace space, IVector2 pos) {
		ISpaceObject[] gravitationFields = space.getSpaceObjectsByType(ManageGravitationProcess.GRAVITATION_FIELD);
		for (int i = 0; i < gravitationFields.length; i++) {
			if (pos.equals(gravitationFields[i].getProperty(Space2D.PROPERTY_POSITION))) {
				return new Vector2Int((IVector2) gravitationFields[i].getProperty(ManageGravitationProcess.GRAVITATION_CENTER_POS));
			}
		}
		return null;
	}

	@Override
	public void componentAdded(IComponentDescription component,
			IEnvironmentSpace space) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentRemoved(IComponentDescription component,
			IEnvironmentSpace space) {
		// TODO Auto-generated method stub
		
	}
}
