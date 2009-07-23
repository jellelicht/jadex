package jadex.bdi.bpmn;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Write a parameter (set) value.
 *  The parameter(set) is specified by the 'parameter(set)name' parameter.
 *  The value is specified by the 'value' parameter.
 *  For parameter sets a mode 'add', 'remove', or 'removeAll' can be specified to distinguish
 *  between value addition (default) and removal.
 */
public class WriteParameterTask extends AbstractTask
{
	//-------- constants --------
	
	/** The 'add' mode (default). */
	public static final String	MODE_ADD	= "add";
	
	/** The 'remove' mode. */
	public static final String	MODE_REMOVE	= "remove";
	
	/** The 'removeAll' mode. */
	public static final String	MODE_REMOVE_ALL	= "removeAll";
	
	//-------- AbstractTask methods --------
	
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IProcessInstance instance)
	{
		BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
		
		if(context.hasParameterValue("parametername"))
		{
			String name = (String)context.getParameterValue("parametername");
			Object value = context.getParameterValue("value");
			inst.getParameter(name).setValue(value);
		}
		else if(context.hasParameterValue("parametersetname"))
		{
			String name = (String)context.getParameterValue("parametersetname");
			if(!context.hasParameterValue("mode") || MODE_ADD.equals(context.getParameterValue("mode")))
			{
				Object value = context.getParameterValue("value");
				inst.getParameterSet(name).addValue(value);
			}
			else if(MODE_REMOVE.equals(context.getParameterValue("mode")))
			{
				Object value = context.getParameterValue("value");
				inst.getParameterSet(name).removeValue(value);
			}
			else if(MODE_REMOVE_ALL.equals(context.getParameterValue("mode")))
			{
				inst.getParameterSet(name).removeValues();
			}
			else
			{
				throw new RuntimeException("Unknown mode: "+context.getParameterValue("mode")+", "+context);
			}
		}
		else
		{
			throw new RuntimeException("Parameter(set)name no specified: "+context);
		}
	}
}
