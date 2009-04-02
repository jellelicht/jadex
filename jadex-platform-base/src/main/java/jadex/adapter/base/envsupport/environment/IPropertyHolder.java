package jadex.adapter.base.envsupport.environment;

import java.beans.PropertyChangeListener;
import java.util.Map;

/** Interface for property-holding objects.
 */
public interface IPropertyHolder
{
	/** 
	 * Returns the monitor.
	 * @return the monitor
	 */
	public Object getMonitor();
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name);
	
	/**
	 * Returns all of the properties.
	 * @return the properties
	 */
	public Map getProperties();
	
	/**
	 * Sets a property
	 * @param name name of the property
	 * @param value value of the property
	 */
	public void setProperty(String name, Object value);
	
	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
