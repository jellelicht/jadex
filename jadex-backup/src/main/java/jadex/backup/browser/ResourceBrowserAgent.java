package jadex.backup.browser;

import jadex.backup.resource.FileMetaInfo;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *  Simple gui to view available resources.
 */
@Agent
public class ResourceBrowserAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	component;
	
	/** The gui frame. */
	protected JFrame	gui;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	ea	= component.getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JTree	tree	= new JTree(new ResourceTreeModel(ea));
				tree.setCellRenderer(new DefaultTreeCellRenderer()
				{
					public Component getTreeCellRendererComponent(JTree tree,
							Object value, boolean sel, boolean expanded,
							boolean leaf, int row, boolean hasFocus)
					{
						if(value instanceof Tuple2)
						{
							FileMetaInfo fi	= (FileMetaInfo)((Tuple2<?,?>)value).getFirstEntity();
							if("/".equals(fi.getPath()))
							{
								IResourceService	res	= (IResourceService)((Tuple2<?,?>)value).getSecondEntity();
								value	= res.getResourceId() +" ("+res.getLocalId()+")";
							}
							else
							{
								value	= fi.getPath();
							}
						}
						
						return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					}
				});
				
				gui	= new JFrame("Jadex Backup - Resource Browser");
				gui.getContentPane().add(new JScrollPane(tree));
				gui.setSize(800, 600);
				gui.setLocation(SGUI.calculateMiddlePosition(gui));
				gui.setVisible(true);
				gui.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						ea.killComponent();
					}
				});
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public IFuture<Void>	stop()
	{
		final Future<Void>	ret	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
				ret.setResult(null);
			}
		});
		return ret;
	}
}
