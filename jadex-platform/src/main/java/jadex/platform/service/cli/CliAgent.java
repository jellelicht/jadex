package jadex.platform.service.cli;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.gui.SGUI;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/**
 *  The client agent allows for executing command line commands.
 *  
 *  It offers the executeCommand() method via the ICliService.
 */
@Agent
@Service
@ProvidedServices(
{
	@ProvidedService(name="cliser", type=ICliService.class, implementation=@Implementation(expression="$pojoagent")),
	@ProvidedService(type=IInternalCliService.class, implementation=@Implementation(expression="$component.getRawService(\"cliser\")"))
})
public class CliAgent implements ICliService, IInternalCliService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The shells per session. */
	protected Map<Tuple2<String, Integer>, CliShell> shells;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		shells = new HashMap<Tuple2<String, Integer>, CliShell>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JTextField tf = new JTextField(20);
				final JTextArea ta = new JTextArea(20, 20);
				ta.setEditable(false);

				final Tuple2<String, Integer> guisess = new Tuple2<String, Integer>(SUtil.createUniqueId("guisess"), new Integer(0));
				tf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						agent.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ICliService clis = (ICliService)ia.getServiceContainer().getProvidedServices(ICliService.class)[0];
								String txt = tf.getText();
								ta.append(txt+SUtil.LF);
								tf.setText("");
								clis.executeCommand(txt, guisess).addResultListener(new IResultListener<String>()
								{
									public void resultAvailable(String result)
									{
										ta.append(result+SUtil.LF);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										ta.append(exception.getMessage()+SUtil.LF);
									}
								});
								return IFuture.DONE;
							}
						});
					}
				});
				JPanel p = new JPanel(new BorderLayout());
				p.add(ta, BorderLayout.CENTER);
				p.add(tf, BorderLayout.SOUTH);
				JFrame f = new JFrame();
				f.add(p, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
			}
		});
		
		Runnable reader = new Runnable()
		{
			public void run()
			{
				ThreadSuspendable sus = new ThreadSuspendable();
				final Tuple2<String, Integer> consess = new Tuple2<String, Integer>(SUtil.createUniqueId("consess"), new Integer(0));
				System.out.println(getShell(consess).getShellPrompt().get(sus));
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
				while(true)
				{
					try
					{
						final String tmp = br.readLine();
						final String cmd = tmp.endsWith(";")? tmp.substring(0, tmp.length()-1): tmp;
						if("exit".equals(cmd))
							break;
						
						agent.scheduleStep(new IComponentStep<Void>()
						{
							public jadex.commons.future.IFuture<Void> execute(IInternalAccess ia) 
							{
								final Future<Void> ret = new Future<Void>();
								
								executeCommand(cmd, consess).addResultListener(new IResultListener<String>()
								{
									public void resultAvailable(String result)
									{
										if(result!=null)
											System.out.println(result);
										printPrompt();
									}
									
									public void exceptionOccurred(Exception exception)
									{
										System.out.println("Invocation error: "+exception.getMessage());
										printPrompt();
									}
									
									protected void printPrompt()
									{
										getShell(consess).getShellPrompt().addResultListener(new DefaultResultListener<String>()
										{
											public void resultAvailable(String result)
											{
												System.out.println(result);
												ret.setResult(null);
											}
										});
									}
								});
								
								return ret;
							}
						}).get(new ThreadSuspendable());
					}
					catch(IOException ioe)
					{
						ioe.printStackTrace();
					}
				}
			}
		};
		Thread t = new Thread(reader);
		t.start();
	}
	
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(String line, Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).executeCommand(line);
	}
	
	/**
	 * 
	 */
	public IFuture<String> internalGetShellPrompt(Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).internalGetShellPrompt();
	}
	
	/**
	 * 
	 */
	public IFuture<Boolean> removeSubshell(Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).removeSubshell();
	}
	
	/**
	 * 
	 */
	public IFuture<Void> addAllCommandsFromClassPath(Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).addAllCommandsFromClassPath();
	}
	
	/**
	 * 
	 */
	public IFuture<Void> addCommand(ICliCommand cmd, Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).addCommand(cmd);
	}
	
	/**
	 *  Get the shell.
	 *  @param session The session.
	 *  @return The shell.
	 */
	public CliShell getShell(Tuple2<String, Integer> sessionid)
	{
		if(sessionid==null)
			throw new IllegalArgumentException("Must not null");
		
		// todo: remove obsolete shells
		
		CliShell shell = shells.get(sessionid);
		if(shell==null)
		{
			System.out.println("created new shell for session: "+sessionid);
			shell = new CliShell(agent.getExternalAccess(), agent.getExternalAccess().getComponentIdentifier().getRoot().getName(), sessionid);
			shell.addAllCommandsFromClassPath(); // agent.getClassLoader()
			shells.put(sessionid, shell);
		}
		return shell;
	}

}
