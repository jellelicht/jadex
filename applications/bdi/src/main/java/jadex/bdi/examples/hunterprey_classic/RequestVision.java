/*
 * RequestVision.java Generated by Protege plugin Beanynizer. Changes will be lost!
 */
package jadex.bdi.examples.hunterprey_classic;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestVision of hunterprey_beans ontology.
 */
public class RequestVision implements IComponentAction
{
	//-------- attributes ----------

	/** The current vision of the creature. */
	protected Vision vision;

	/** The creature. */
	protected Creature creature;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestVision</code>.
	 */
	public RequestVision()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new RequestVision.<br>
	 *  Initializes the object with required attributes.
	 * @param creature
	 */
	public RequestVision(Creature creature)
	{
		this();
		setCreature(creature);
	}

	//-------- accessor methods --------

	/**
	 *  Get the vision of this RequestVision.
	 *  The current vision of the creature.
	 * @return vision
	 */
	public Vision getVision()
	{
		return this.vision;
	}

	/**
	 *  Set the vision of this RequestVision.
	 *  The current vision of the creature.
	 * @param vision the value to be set
	 */
	public void setVision(Vision vision)
	{
		this.vision = vision;
	}

	/**
	 *  Get the creature of this RequestVision.
	 *  The creature.
	 * @return creature
	 */
	public Creature getCreature()
	{
		return this.creature;
	}

	/**
	 *  Set the creature of this RequestVision.
	 *  The creature.
	 * @param creature the value to be set
	 */
	public void setCreature(Creature creature)
	{
		this.creature = creature;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestVision.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestVision(" + "creature=" + getCreature() + ")";
	}
}
