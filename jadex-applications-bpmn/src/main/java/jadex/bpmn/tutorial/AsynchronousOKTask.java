package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *  A task that displays a message using a JOptionPane.
 */
public class AsynchronousOKTask	 implements ITask
{
	protected JDialog	dialog = new JDialog((JDialog)null, false);
	
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(ITaskContext context, BpmnInterpreter process)
	{
		final Future<Void> ret = new Future<Void>();
		
		String	message	= (String)context.getParameterValue("message");
		String	title	= (String)context.getParameterValue("title");
		int	offset	= context.hasParameterValue("y_offset")
			? ((Integer)context.getParameterValue("y_offset")).intValue() : 0;
		
		JOptionPane	pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
		dialog.setTitle(title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setContentPane(pane);
		dialog.pack();
		Point	loc	= SGUI.calculateMiddlePosition(dialog);
		dialog.setLocation(loc.x, loc.y+offset);
		
		pane.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String	prop	= e.getPropertyName();
				if(prop.equals(JOptionPane.VALUE_PROPERTY))
				{
	                dialog.setVisible(false);
//	                listener.resultAvailable(this, null);
	                ret.setResult(null);
				}
			}
	    });

		
		dialog.setVisible(true);
	
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> compensate(final BpmnInterpreter instance)
	{
		final Future<Void> ret = new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				dialog.setVisible(false);
				ret.setResult(null);
			}
		});
		return ret;
	}
}
