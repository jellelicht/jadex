package jadex.wfms.simulation;

import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWfmsListener;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessFinishedEvent;
import jadex.wfms.client.WorkitemQueueChangeEvent;
import jadex.wfms.service.IClientService;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateholder.AbstractNumericStateSet;
import jadex.wfms.simulation.stateholder.BooleanStateSet;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.NumberRange;
import jadex.wfms.simulation.stateholder.ProcessStateController;
import jadex.wfms.simulation.stateholder.StringStateSet;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ClientSimulator implements IClient
{
	private IClientService clientService;
	
	private SimulationWindow simWindow;
	
	private String userName;
	
	private ProcessStateController activeStateController;
	
	private ClientProcessMetaModel clientProcessMetaModel;
	
	public ClientSimulator(IClientService clientService)
	{
		this(clientService, "TestUser");
	}
	
	public ClientSimulator(IClientService clientSrv, String userName)
	{
		this.activeStateController = null;
		this.clientService = clientSrv;
		this.userName = userName;
		clientService.authenticate(this);
		simWindow = new SimulationWindow();
		
		setupActions();
		
		this.clientService.getMonitoringService(this).addLogHandler(this, new Handler()
		{
			
			public void publish(final LogRecord record)
			{
				if (EventQueue.isDispatchThread())
					simWindow.addLogMessage(record.getMessage());
				else
				{
					EventQueue.invokeLater(new Runnable()
					{
					
						public void run()
						{
							simWindow.addLogMessage(record.getMessage());
						}
					});
				}
			}
			
			public void flush()
			{
			}
			
			public void close() throws SecurityException
			{
			}
		});
		
		clientService.addWfmsListener(new IWfmsListener()
		{
			
			public void workitemRemoved(WorkitemQueueChangeEvent event)
			{
			}
			
			public void workitemAdded(WorkitemQueueChangeEvent event)
			{
				System.out.println("New workitem: " + event.getWorkitem().getName());
				int type = event.getWorkitem().getType();
				IClientActivity activity = clientService.beginActivity(ClientSimulator.this, event.getWorkitem());
				if (type == IWorkitem.TEXT_INFO_WORKITEM_TYPE)
				{
					simWindow.addLogMessage("Processing Info Activity: " + activity.getName());
				}
				else if (type == IWorkitem.DATA_FETCH_WORKITEM_TYPE)
				{
					Map parameterStates = activeStateController.getActivityState(activity.getName());
					activity.setParameterValues(parameterStates);
				}
				
				clientService.finishActivity(ClientSimulator.this, activity);
			}
			
			public void processFinished(ProcessFinishedEvent event)
			{
				if ((activeStateController == null) || (activeStateController.finalState()))
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							simWindow.enableMenuItem(SimulationWindow.STOP_MENU_ITEM_NAME, false);
							simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, true);
							activeStateController = null;
							simWindow.addLogMessage("Finished Simulation");
						}
					});
				}
				else
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							activeStateController.nextState();
							simWindow.addLogMessage("Setting new process state: " + activeStateController.toString());
							clientService.startProcess(ClientSimulator.this, clientProcessMetaModel.getMainProcessName());
						}
					});
				}
			}
			
			public IClient getClient()
			{
				return null;
			}
		});
	}
	
	public IClientService getClientService()
	{
		return clientService;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	private void updateGui()
	{
		if (clientProcessMetaModel != null)
		{
			long stateCount = clientProcessMetaModel.createProcessStateController().getStateCount();
			if (stateCount > 0)
				simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, true);
			else
				simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, false);
			simWindow.setStatusBar("Process States: " + String.valueOf(stateCount));
		}
		else
			simWindow.setStatusBar(" ");
		simWindow.refreshParameterStates();
	}
	
	private void setupActions()
	{
		simWindow.setMenuItemAction(SimulationWindow.OPEN_MENU_ITEM_NAME, new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				Set modelNames = clientService.getModelNames(ClientSimulator.this);
				String modelName = simWindow.showProcessPickerDialog(modelNames);
				System.out.println(modelName);
				if (modelName == null)
					return;
				ClientProcessMetaModel model = new ClientProcessMetaModel();
				try
				{
					model.setRootModel(modelName, clientService.getProcessDefinitionService(ClientSimulator.this).getProcessModel(ClientSimulator.this, modelName));
					simWindow.setProcessTreeModel(model);
					model.addStateChangeListener(new ChangeListener()
					{
						public void stateChanged(ChangeEvent e)
						{
							updateGui();
						}
					});
					clientProcessMetaModel = model;
					updateGui();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					simWindow.showMessage(JOptionPane.ERROR_MESSAGE, "Cannot open the process", "Opening the process failed.");
				}
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.CLOSE_MENU_ITEM_NAME, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				simWindow.setProcessTreeModel(null);
				clientProcessMetaModel = null;
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.START_MENU_ITEM_NAME, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				simWindow.enableMenuItem(SimulationWindow.START_MENU_ITEM_NAME, false);
				simWindow.enableMenuItem(SimulationWindow.STOP_MENU_ITEM_NAME, true);
				activeStateController = clientProcessMetaModel.createProcessStateController();
				simWindow.addLogMessage("Starting Simulation");
				simWindow.addLogMessage("Setting process state: " + activeStateController.toString());
				clientService.startProcess(ClientSimulator.this, clientProcessMetaModel.getMainProcessName());
			}
		});
		
		simWindow.setMenuItemAction(SimulationWindow.AUTO_FILL_MENU_ITEM_NAME, new AbstractAction() {
			
			private Random random;
			
			public void actionPerformed(ActionEvent e)
			{
				if (random == null)
					random = new Random();
				if (clientProcessMetaModel != null)
				{
					List pSets = clientProcessMetaModel.getParameterSets();
					for (Iterator it = pSets.iterator(); it.hasNext(); )
					{
						IParameterStateSet pSet = (IParameterStateSet) it.next();
						if (pSet instanceof BooleanStateSet)
						{
							if (((BooleanStateSet) pSet).hasState(Boolean.FALSE))
								((BooleanStateSet) pSet).addState(Boolean.TRUE);
							else
								((BooleanStateSet) pSet).addState(Boolean.FALSE);
						}
						else if (pSet instanceof AbstractNumericStateSet)
						{
							long number = Math.abs(random.nextLong()) % ((AbstractNumericStateSet) pSet).getUpperBound();
							((AbstractNumericStateSet) pSet).addRange(new NumberRange(number, number));
						}
						else if (pSet instanceof StringStateSet)
							((StringStateSet) pSet).addString("Quick Fill Test " + String.valueOf(random.nextLong()));
					}
				}
			}
		});
	}
	private Timer timer;
}
