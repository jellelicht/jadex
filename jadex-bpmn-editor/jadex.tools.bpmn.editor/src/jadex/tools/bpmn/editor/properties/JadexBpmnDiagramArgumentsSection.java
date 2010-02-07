/**
 * 
 */
package jadex.tools.bpmn.editor.properties;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexBpmnDiagramArgumentsSection extends
		AbstractMultiColumnTablePropertySection
{

	public static final String label = "Arguments";
	public static final String[] fields = new String[] {"Name", "Description", "Typename", "Value"};
	public static final int[] columnWeights = new int[] {1,1,1,8};
	public static final String[] defaultListElementAttributeValues = new String[]{"name", "description", "Object", ""};
	public static final int uniqueListElementAttribute = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramArgumentsSection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_ARGUMENTS_LIST_DETAIL,
				"Arguments", fields, columnWeights, defaultListElementAttributeValues, uniqueListElementAttribute);
	}

}
