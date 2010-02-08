package jadex.tools.simcenter;

import jadex.adapter.base.ISimulationService;
import jadex.commons.SGUI;
import jadex.service.clock.IClock;
import jadex.tools.common.ToolTipAction;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *	The context panel shows the settings for an execution context.
 */
public class ContextPanel extends AbstractTimePanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		// todo: rename icon?
		"start",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/start.png"),
		"step_event",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/single_step_event.png"),
		"step_time",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/single_step_time.png"),
		"pause",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/pause.png")
	});
	
	//-------- attributes --------

	/** The execution context. */
	//protected ExecutionContext context;

	/** The sim center panel. */
	protected SimCenterPanel simp;
	
//	/** The run button. */
//	protected JButton run;
//	
//	/** The event step button. */
//	protected JButton estep;
//
//	/** The time step button. */
//	protected JButton tstep;
//	
//	/** The pause button. */
//	protected JButton pause;
	
	//-------- constructors --------

	/**
	 *  Create a context panel.
	 *  @param context The execution context.
	 */
	public ContextPanel(SimCenterPanel simp)
	{
		super(simp.getServiceContainer());
		this.setLayout(new FlowLayout());
		this.simp = simp;
		
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Execution Control "));
		
//		this.run = new JButton(START);
//		this.estep = new JButton(STEP_EVENT);
//		this.tstep = new JButton(STEP_TIME);
//		this.pause= new JButton(PAUSE);
//		run.setMargin(new Insets(0,0,0,0));
//		estep.setMargin(new Insets(0,0,0,0));
//		tstep.setMargin(new Insets(0,0,0,0));
//		pause.setMargin(new Insets(0,0,0,0));
		
		JToolBar	toolbar	= new JToolBar("Simulation Control");
		toolbar.add(START);
		toolbar.add(STEP_EVENT);
		toolbar.add(STEP_TIME);
		toolbar.add(PAUSE);
		this.add(toolbar);
		
//		int x=0;
//		int y=0;
//		this.add(run, new GridBagConstraints(x++,y,1,1,1,0,
//			GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
//		this.add(estep, new GridBagConstraints(x++,y,1,1,0,0,
//			GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
//		this.add(tstep, new GridBagConstraints(x++,y,1,1,0,0,
//			GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
//		this.add(pause, new GridBagConstraints(x++,y,1,1,1,0,
//			GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));

		setActive(true);
	}
	
	/**
	 *  Update the view.
	 */
	public void updateView()
	{
//		System.out.println("uv1: start="+START.isEnabled()+" step="+STEP_EVENT.isEnabled()+" time="+STEP_TIME.isEnabled()+" pause="+PAUSE.isEnabled());
		
		// todo: use event information for updating
		
		START.setEnabled(START.isEnabled());
		STEP_EVENT.setEnabled(STEP_EVENT.isEnabled());
		STEP_TIME.setEnabled(STEP_TIME.isEnabled());
		PAUSE.setEnabled(PAUSE.isEnabled());
//		System.out.println("uv2: start="+START.isEnabled()+" step="+STEP_EVENT.isEnabled()+" time="+STEP_TIME.isEnabled()+" pause="+PAUSE.isEnabled());
	}
	
	/**
	 *  Start action.
	 */
	public final Action START = new ToolTipAction(null, icons.getIcon("start"),
		"Start the execution of the application")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				getSimulationService().startService();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		/**
		 *  Test if action is enabled.
		 *  @return True, if enabled.
		 */
		public boolean isEnabled()
		{
			boolean clockok = getClockService().getNextTimer()!=null 
				|| getClockService().getClockType().equals(IClock.TYPE_CONTINUOUS)
				|| getClockService().getClockType().equals(IClock.TYPE_SYSTEM);
			return !getSimulationService().isExecuting() && clockok;
		}
	};
	
	/**
	 *  Step action.
	 */
	public final Action STEP_EVENT = new ToolTipAction(null, icons.getIcon("step_event"),
		"Execute one timer entry.")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				getSimulationService().stepEvent();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		/**
		 *  Test if action is enabled.
		 *  @return True, if enabled.
		 */
		public boolean isEnabled()
		{
			boolean clockok = getClockService().getNextTimer()!=null; 
			return !getClockService().getClockType().equals(IClock.TYPE_CONTINUOUS) 
				&& !getClockService().getClockType().equals(IClock.TYPE_SYSTEM) 
				&& !getSimulationService().isExecuting() && clockok;
		}
	};
	
	/**
	 *  Time step action.
	 */
	public final Action STEP_TIME = new ToolTipAction(null, icons.getIcon("step_time"),
		"Execute all timer entries belonging to the current time point.")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				getSimulationService().stepTime();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		/**
		 *  Test if action is enabled.
		 *  @return True, if enabled.
		 */
		public boolean isEnabled()
		{
			boolean clockok = getClockService().getNextTimer()!=null;
			return !getClockService().getClockType().equals(IClock.TYPE_CONTINUOUS)
				&& !getClockService().getClockType().equals(IClock.TYPE_SYSTEM)
				&& !getSimulationService().isExecuting() && clockok;
		}
	};
	
	/**
	 *  Pause the current execution.
	 */
	public final Action PAUSE = new ToolTipAction(null , icons.getIcon("pause"),
		"Pause the current execution.")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				getSimulationService().pause();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
		
		/**
		 *  Test if action is enabled.
		 *  @return True, if enabled.
		 */
		public boolean isEnabled()
		{
			return getSimulationService().isExecuting() && getSimulationService().getMode()
				.equals(ISimulationService.MODE_NORMAL);
		}
	};
}