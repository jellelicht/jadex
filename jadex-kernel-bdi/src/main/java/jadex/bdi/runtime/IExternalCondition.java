package jadex.bdi.runtime;

/* $if !android $ */
import java.beans.PropertyChangeListener;
/* $else $
import javaa.beans.PropertyChangeListener;
$endif $ */

/**
 *  A condition that a plan can wait for.
 *  Has to generate bean property change events
 *  on changes, such that the condition can be
 *  reevaluated.
 */
public interface IExternalCondition
{
	/**
	 *  Test if the condition holds.
	 */
	public boolean isTrue();
	
	/**
	 *  Add a property change listener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 *  Remove a property change listener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
}
