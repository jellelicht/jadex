package jadex.micro.examples.mandelbrot;

import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.ChangeEvent;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 *  Agent offering a display service.
 */
@Description("Agent offering a display service.")
@ProvidedServices(@ProvidedService(type=IDisplayService.class, expression="new DisplayService($component)"))
@RequiredServices({
	@RequiredService(name="generateservice", type=IGenerateService.class),
	@RequiredService(name="progressservice", type=IProgressService.class),
	@RequiredService(name="cmsservice", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class DisplayAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The GUI. */
	protected DisplayPanel	panel;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		// Hack!!! Swing code not on swing thread!?
		DisplayAgent.this.panel	= new DisplayPanel(getExternalAccess());

//		addService(new DisplayService(this));
		
		final IExternalAccess	access	= getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JFrame	frame	= new JFrame(getAgentName());
				JScrollPane	scroll	= new JScrollPane(panel);

				JTextPane helptext = new JTextPane();
				helptext.setText(DisplayPanel.HELPTEXT);
				helptext.setEditable(false);
				JPanel	right	= new JPanel(new BorderLayout());
				right.add(new ColorChooserPanel(panel), BorderLayout.CENTER);
				right.add(helptext, BorderLayout.NORTH);

				
				JSplitPane	split	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, right);
				split.setResizeWeight(1);
				split.setOneTouchExpandable(true);
				split.setDividerLocation(375);
				frame.getContentPane().add(BorderLayout.CENTER, split);
				frame.setSize(500, 400);
				frame.setLocation(SGUI.calculateMiddlePosition(frame));
				frame.setVisible(true);
				
				frame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						access.killComponent();
					}
				});
				
				access.scheduleStep(new IComponentStep()
				{
					@XMLClassname("dispose")
					public Object execute(IInternalAccess ia)
					{
						ia.addComponentListener(new IComponentListener()
						{
							public void componentTerminating(ChangeEvent ce)
							{
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										frame.dispose();
									}
								});
							}
							
							public void componentTerminated(ChangeEvent ce)
							{
							}
						});
						
						return null;
					}
				});
			}
		});
		
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the display panel.
	 */
	public DisplayPanel	getPanel()
	{
		return this.panel;
	}
}
