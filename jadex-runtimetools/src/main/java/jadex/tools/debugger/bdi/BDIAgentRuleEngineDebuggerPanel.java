package jadex.tools.debugger.bdi;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.ICommand;
import jadex.commons.ISteppable;
import jadex.commons.SGUI;
import jadex.rules.tools.reteviewer.RetePanel;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.debugger.IDebuggerPanel;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

/**
 *  Show the rule engine of a BDI agent.
 */
public class BDIAgentRuleEngineDebuggerPanel	implements IDebuggerPanel
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"show_rete", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bug_small.png")
	});

	//-------- IDebuggerPanel methods --------
	
	/** The gui component. */
	protected JComponent	retepanel;

	//-------- IDebuggerPanel methods --------

	/**
	 *  Called to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param id	The component identifier.
	 * 	@param access	The external access of the component.
	 */
	public void init(IControlCenter jcc, IComponentIdentifier name, Object access)
	{
		BDIInterpreter bdii = ((ElementFlyweight)access).getInterpreter();
		this.retepanel = new RetePanel(bdii.getRuleSystem(), new ISteppable()
		{
			
			public void setStepmode(boolean stepmode)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void removeBreakpoint(Object markerobj)
			{
				// TODO Auto-generated method stub
				
			}
			
			public boolean isStepmode()
			{
				// TODO Auto-generated method stub
				return false;
			}
			
			public boolean isBreakpoint(Object markerobj)
			{
				// TODO Auto-generated method stub
				return false;
			}
			
			public void doStep()
			{
				// TODO Auto-generated method stub
				
			}
			
			public void addBreakpointCommand(ICommand command)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void addBreakpoint(Object markerobj)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 *  The title of the panel (name of the tab).
	 *  @return	The tab title.
	 */
	public String getTitle()
	{
		return "Rule Engine";
	}

	/**
	 *  The icon of the panel.
	 *  @return The icon (or null, if none).
	 */
	public Icon getIcon()
	{
		return icons.getIcon("show_rete");
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return retepanel;
	}
	
	/**
	 *  The tooltip text of the panel, if any.
	 *  @return The tooltip text, or null.
	 */
	public String getTooltipText()
	{
		return "Show the rule engine.";
	}

}
