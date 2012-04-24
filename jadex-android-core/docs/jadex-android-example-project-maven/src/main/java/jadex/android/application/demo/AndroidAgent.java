package jadex.android.application.demo;

import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.android.IAndroidContextService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Map;

import android.util.Log;

/**
 *  Simple example agent that shows messages
 *  when it is started, stopped and when it receives a message. 
 */
@Description("Sample Android Agent.")
@RequiredServices({
		@RequiredService(name="androidcontext", type=IAndroidContextService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})
public class AndroidAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Called when the agent is started.
	 */
	public IFuture<Void> executeBody()
	{
		showAndroidMessage("This is Agent <<" + this.getAgentName() + ">> saying hello!");
		return new Future<Void>();
	}

	/**
	 *  Called when the agent is killed.
	 */
	public IFuture<Void> agentKilled()
	{
		showAndroidMessage("This is Agent <<" + this.getAgentName() + ">> saying goodbye!");
		return IFuture.DONE;
	}

	/**
	 *  Called when the agent receives a message.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		showAndroidMessage(msg.get(SFipa.PERFORMATIVE)+"("+msg.get(SFipa.CONTENT)+")");
	}
	
	//-------- helper methods --------

	/**
	 *	Show a message on the device.  
	 *  @param msg The message to be shown.
	 */
	protected void showAndroidMessage(String msg)
	{
		final ShowToastEvent event = new ShowToastEvent();
		event.setMessage(msg);
		getRequiredService("androidcontext").addResultListener(new DefaultResultListener<Object>() {

			@Override
			public void resultAvailable(Object result) {
				IAndroidContextService contextService = (IAndroidContextService) result;
				boolean dispatchUiEvent = contextService.dispatchUiEvent(event);
				Log.d("Agent", "dispatched: " + dispatchUiEvent);
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
			}
		});
	}
}
