package jadex.tools.serviceviewer;

import jadex.commons.SGUI;
import jadex.tools.common.plugin.AbstractJCCPlugin;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIDefaults;

public class ServiceViewerPlugin extends AbstractJCCPlugin
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"service_viewer", SGUI.makeIcon(ServiceViewerPlugin.class, "/jadex/tools/serviceviewer/images/configure.png")
	});
	
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  Return the unique name of this plugin.
	 *  This method may be called before init().
	 *  Used e.g. to store properties of each plugin.
	 */
	public String getName()
	{
		return "ServiceViewerPlugin";
	}

	/**
	 *  Return the icon representing this plugin.
	 *  This method may be called before init().
	 */
	public Icon getToolIcon(boolean selected)
	{
		return icons.getIcon("service_viewer");
	}

	/**
	 *  Return the id for the help system
	 *  This method may be called before init().
	 */
	public String getHelpID()
	{
		return null;
	}
	
//	/**
//	 *  Create main panel.
//	 *  @return The main panel.
//	 */
//	public JComponent createView()
//	{
//		JTree 
//		
//		
//		JTabbedPane views	= new JTabbedPane();
//		
//		JSplitPane	split	= new JSplitPane();
//		split.add(views);
//		split.add(new JLabel("search"));
//	}
}
