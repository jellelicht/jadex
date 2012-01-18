package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.annotation.CreationCondition;
import jadex.bdiv3.annotation.Goal;
import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.annotations.Event;

@Goal 
public class HelloGoal
{
	protected String text;
	
	public HelloGoal(String text)
	{
		this.text = text;
	}
	
//	@CreationCondition()
	@Condition("creation")
	protected static boolean create(@Event("sayhello") boolean sayhello)
	{
		return sayhello;
	}
}