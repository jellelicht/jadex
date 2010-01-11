package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestAuth;

public class ConnectPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestAuth reqAuth = new RequestAuth((String) getParameter("user_name").getValue(), getParameter("auth_token").getValue()); 
		
		IGoal authGoal = createGoal("reqcap.rp_initiate");
		authGoal.getParameter("action").setValue(reqAuth);
		authGoal.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(authGoal);
		
		IGoal hbGoal = createGoal("keep_sending_heartbeats");
		dispatchTopLevelGoal(hbGoal);
		System.out.println("Goal Active: " + String.valueOf(hbGoal.isActive()));
	}
}
