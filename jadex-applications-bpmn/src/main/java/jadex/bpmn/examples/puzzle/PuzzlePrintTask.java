package jadex.bpmn.examples.puzzle;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Print out some text stored in variable test.
 */
public class PuzzlePrintTask extends AbstractTask
{
	/**
	 * 
	 */
	public Object doExecute(ITaskContext context, IProcessInstance instance)
	{
		int	indent	= ((Number)context.getParameterValue("indent")).intValue();
        for(int x=0; x<indent; x++)
            System.out.print(" ");

		String text = (String)context.getParameterValue("text");
		System.out.println(text);
		return null;
	}
}
