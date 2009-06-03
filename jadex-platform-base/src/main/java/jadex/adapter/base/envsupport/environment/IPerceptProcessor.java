package jadex.adapter.base.envsupport.environment;

import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ISpace;
import jadex.commons.IPropertyObject;

/**
 *  Interface for percept processors.
 *  A percept processor is responsible to process the
 *  percepts generated by percept generators and feed the
 *  percept into the agent (e.g. into its beliefs).
 */
public interface IPerceptProcessor extends IPropertyObject
{
	/**
	 *  Process a new percept.
	 *  @param space The space.
	 *  @param type The type.
	 *  @param percept The percept.
	 *  @param agent The agent identifier.
	 */
	public void processPercept(ISpace space, String type, Object percept, IAgentIdentifier agent);
}
