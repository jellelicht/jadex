package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.commons.xml.TypeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An AGR space description.
 */
public class MAGRSpaceType	extends MSpaceType
{
	//-------- attributes --------
	
	/** The grouptypes. */
	protected List	grouptypes;

	//-------- methods --------
		
	/**
	 *  Get the group types of this space type.
	 *  @return An array of group types (if any).
	 */
	public MGroupType[] getMGroupTypes()
	{
		return grouptypes==null ? null :
			(MGroupType[])grouptypes.toArray(new MGroupType[grouptypes.size()]);
	}

	/**
	 *  Add a group type to this space type.
	 *  @param grouptype	The group type to add. 
	 */
	public void addMGroupType(MGroupType grouptype)
	{
		if(grouptypes==null)
			grouptypes	= new ArrayList();
		grouptypes.add(grouptype);
	}

	/**
	 *  Remove a group type from this space type.
	 *  @param grouptype	The group type to remove. 
	 */
	public void removeMGroupType(MGroupType grouptype)
	{
		if(grouptypes!=null)
		{
			grouptypes.remove(grouptype);
			if(grouptypes.isEmpty())
				grouptypes	= null;
		}
	}
	
	/**
	 *  Get a string representation of this AGR space type.
	 *  @return A string representation of this AGR space type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		if(grouptypes!=null)
		{
			sbuf.append(", grouptypes=");
			sbuf.append(grouptypes);
		}
		sbuf.append(")");
		return sbuf.toString();
	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		types.add(new TypeInfo("agrspacetype", MAGRSpaceType.class));
		types.add(new TypeInfo("grouptype", MGroupType.class));
		types.add(new TypeInfo("role", MRoleType.class));
		return types;
	}
}
