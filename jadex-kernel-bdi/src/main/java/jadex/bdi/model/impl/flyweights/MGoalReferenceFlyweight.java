package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMGoalReference;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for goal reference model.
 */
public class MGoalReferenceFlyweight extends MElementReferenceFlyweight implements IMGoalReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MGoalReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
