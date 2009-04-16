package jadex.adapter.base.envsupport;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;


/**
 * 
 */
public class MEnvTiledLayer
{
	//-------- attributes --------

	/** The width. */
	protected double width;
	
	/** The height. */
	protected double height;
	
	/** The image path. */
	protected String imagepath;

	//-------- methods --------
	
	/**
	 *  Get the width.
	 *  @return The width.
	 */
	public double getWidth()
	{
		return this.width;
	}

	/**
	 *  Set the width.
	 *  @param width The width to set.
	 */
	public void setWidth(double width)
	{
		this.width = width;
	}
	
	/**
	 *  Get the height.
	 *  @return The height.
	 */
	public double getHeight()
	{
		return this.height;
	}

	/**
	 *  Set the height.
	 *  @param height The height to set.
	 */
	public void setHeight(double height)
	{
		this.height = height;
	}
	
	/**
	 * @return the imagepath
	 */
	public String getImagePath()
	{
		return this.imagepath;
	}

	/**
	 * @param imagepath the imagepath to set
	 */
	public void setImagePath(String imagepath)
	{
		this.imagepath = imagepath;
	}

	/**
	 * 
	 */
	public IVector2 getSize()
	{
		return width==0 && height==0? Vector2Double.ZERO: new Vector2Double(width, height);
	}
}
