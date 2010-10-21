package deco4mas.examples.agentNegotiation.deco.media;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceProcess;
import jadex.commons.SimplePropertyObject;
import jadex.commons.service.clock.IClockService;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * Process for check up deadline at medium
 */
public class MediumTimeProcess extends SimplePropertyObject implements ISpaceProcess
{
	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space)
	{
		System.out.println("#Start MediumTimeProcess");
	}

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
		// System.out.println("#mediumTimeProcess");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		CoordinationSpace env = (CoordinationSpace) space;
		if (env.activeCoordinationMechanisms.size() > 0)
		{
			for (ICoordinationMechanism coordMechanism : env.activeCoordinationMechanisms)
			{
				if (coordMechanism.getRealisationName().equals("by_neg"))
				{
					NegotiationMechanism mechanism = (NegotiationMechanism) coordMechanism;
					mechanism.nextTick();
				}
			}
		}
	}
}
