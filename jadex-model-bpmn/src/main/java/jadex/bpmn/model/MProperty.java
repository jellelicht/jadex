package jadex.bpmn.model;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  A parameter model element.
 */
public class MProperty extends MAnnotationElement
{
	//-------- attributes --------
	
	/** The clazz. */
	protected ClassInfo clazz;
	
	/** The name. */
	protected String name;
	
	/** The initial value. */
	protected UnparsedExpression initialval; // IParsedExpression

	//-------- constructors --------
	
	/**
	 *  Create a new parameter.
	 */
	public MProperty()
	{
	}
	
	/**
	 *  Create a new parameter.
	 */
	public MProperty(ClassInfo clazz, String name, 
		UnparsedExpression initialval)
	{
		this.clazz = clazz;
		this.name = name;
		this.initialval = initialval;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public ClassInfo getClazz()
	{
		return this.clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the initialval.
	 *  @return The initialval.
	 */
	public UnparsedExpression getInitialValue()
	{
		return this.initialval;
	}

	/**
	 *  Set the initial value.
	 *  @param initialval The initial value to set.
	 */
	public void setInitialValue(UnparsedExpression initialval)
	{
		this.initialval = initialval;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "MParameter(clazz=" + this.clazz + ", initialval=" + this.initialval
			+ ", name=" + this.name + ")";
	}	
}

