package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.envsupport.IObjectCreator;
import jadex.adapter.base.envsupport.MEnvSpaceInstance;
import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IContext;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  
 */
public abstract class AbstractEnvironmentSpace extends PropertyHolder implements IEnvironmentSpace
{
	//-------- attributes --------
	
	/** The space name. */
	protected String name;
	
	/** The context. */
	protected IContext context;
	
	/** Avatar mappings. */
	protected MultiCollection avatarmappings;

	/** Data view mappings. */
	protected MultiCollection	dataviewmappings;
	
	/** Available space actions. */
	protected Map spaceactions;
	
	/** Available views */
	protected Map views;
	
	/** Available agent actions. */
	protected Map agentactions;
	
	/** The environment processes. */
	protected Map processes;
	
	/** The percept generators. */
	protected Map perceptgenerators;

	/** The percept mappings. */
	protected Map perceptmappings;

	/** The space object types. */
	protected Map spaceobjecttypes;
	
	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map spaceobjects;
	
	/** Types of EnvironmentObjects and lists of EnvironmentObjects of that type (typed view). */
	protected Map spaceobjectsbytype;
	
	/** Space object by owner, owner can null (owner view). */
	protected Map spaceobjectsbyowner;
	
	/** Object id counter for new ids. */
	protected AtomicCounter objectidcounter;
	
	/** The environment listeners. */
	protected List listeners;
	
	/** The list of scheduled agent actions. */
	protected AgentActionList	actionlist;
	
	/** The list of scheduled percepts. */
	protected PerceptList	perceptlist;
	
	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		super(new Object());
		this.views = new HashMap();
		this.avatarmappings = new MultiCollection();
		this.dataviewmappings = new MultiCollection();
		this.spaceactions = new HashMap();
		this.agentactions = new HashMap();
		this.processes = new HashMap();
		this.perceptgenerators = new HashMap();
		this.perceptmappings = new HashMap();
		this.spaceobjecttypes = new HashMap();
		this.spaceobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceobjectsbyowner = new HashMap();
		this.objectidcounter = new AtomicCounter();
		this.actionlist	= new AgentActionList(this);
		this.perceptlist	= new PerceptList(this);
	}
	
	//-------- methods --------
	
	/**
	 * Adds a space process.
	 * @param id ID of the space process
	 * @param process new space process
	 */
	public void addSpaceProcess(Object id, ISpaceProcess process)
	{
		synchronized(monitor)
		{
			processes.put(id, process);
//			process.start(this);	// Done by executor.
		}
	}

	/**
	 * Returns a space process.
	 * @param id ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object id )
	{
		synchronized(monitor)
		{
			return (ISpaceProcess)processes.get(id);
		}
	}

	/**
	 * Removes a space process.
	 * @param id ID of the space process
	 */
	public void removeSpaceProcess(final Object id)
	{
		synchronized(monitor)
		{
			ISpaceProcess process = (ISpaceProcess)processes.remove(id);
			if(process!=null)
				process.shutdown(this);
		}
	}
	
	/**
	 *  Add a space type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceObjectType(String typename, Map properties)
	{
		synchronized(monitor)
		{
			spaceobjecttypes.put(typename, properties);
		}
	}
	
	/**
	 *  Remove a space object type.
	 *  @param typename The type name.
	 */
	public void removeSpaceObjectType(String typename)
	{
		synchronized(monitor)
		{
			spaceobjecttypes.remove(typename);
		}
	}

	
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks, List listeners)
	{
		if(!spaceobjecttypes.containsKey(typename))
			throw new RuntimeException("Unknown space object type: "+typename);
			
		ISpaceObject ret;
		
		synchronized(monitor)
		{
			// Generate id.
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			Map typeprops = (Map)spaceobjecttypes.get(typename);
			if(typeprops!=null)
			{
				if(properties==null)
					properties = new HashMap();
				for(Iterator it=typeprops.keySet().iterator(); it.hasNext(); )
				{
					String propname = (String)it.next();
					if(!properties.containsKey(propname))
						properties.put(propname, typeprops.get(propname));
				}
			}
			
			// Create the object.
			ret = new SpaceObject(id, typename, properties, tasks, listeners, monitor, this);
			spaceobjects.put(id, ret);

			// Store in type objects.
			List typeobjects = (List)spaceobjectsbytype.get(ret.getType());
			if(typeobjects == null)
			{
				typeobjects = new ArrayList();
				spaceobjectsbytype.put(ret.getType(), typeobjects);
			}
			typeobjects.add(ret);
			
			// Store in owner objects.
			if(properties!=null && properties.get(ISpaceObject.PROPERTY_OWNER)!=null)
			{
				IAgentIdentifier	owner	= (IAgentIdentifier)properties.get(ISpaceObject.PROPERTY_OWNER);
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(ret);
			}
			
			// Create view(s) for the object if any.
			if(dataviewmappings!=null && dataviewmappings.getCollection(typename)!=null)
			{
				for(Iterator it=dataviewmappings.getCollection(typename).iterator(); it.hasNext(); )
				{
					try
					{
						Map	sourceview	= (Map)it.next();
						Map viewargs = new HashMap();
						viewargs.put("sourceview", sourceview);
						viewargs.put("space", this);
						viewargs.put("$object", ret);
						
						IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs);
						addDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name")+"_"+id, view);
					}
					catch(Exception e)
					{
						if(e instanceof RuntimeException)
							throw (RuntimeException)e;
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		if(listeners!=null)
		{
			EnvironmentEvent event = new EnvironmentEvent(EnvironmentEvent.OBJECT_CREATED, this, ret, null);
			for(int i=0; i<listeners.size(); i++)
			{
				IEnvironmentListener lis = (IEnvironmentListener)listeners.get(i);
				lis.dispatchEnvironmentEvent(event);
			}
		}
		
		return ret;
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param id the object's ID
	 */
	public void destroySpaceObject(final Object id)
	{
		ISpaceObject obj;
		synchronized(monitor)
		{
			obj = (ISpaceObject)spaceobjects.get(id);
			// shutdown and jettison tasks
			obj.clearTasks();

			// remove object
			spaceobjects.remove(id);
			List typeobjs = (List)spaceobjectsbytype.get(obj.getType());
			typeobjs.remove(obj);
			if(typeobjs.size()==0)
				spaceobjectsbytype.remove(obj.getType());
			
			if(obj.getProperty(ISpaceObject.PROPERTY_OWNER)!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(obj.getProperty(ISpaceObject.PROPERTY_OWNER));
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(obj.getProperty(ISpaceObject.PROPERTY_OWNER));
			}
		}
		
		// signal removal
		// hmm? what about calling destroy on object? could it do sth. else than throwing event?
		ObjectEvent event = new ObjectEvent(ObjectEvent.OBJECT_REMOVED);
		event.setParameter("space_name", getName());
		obj.fireObjectEvent(event);
		
		if(listeners!=null)
		{
			EnvironmentEvent ev = new EnvironmentEvent(EnvironmentEvent.OBJECT_DESTROYED, this, obj, null);
			for(int i=0; i<listeners.size(); i++)
			{
				IEnvironmentListener lis = (IEnvironmentListener)listeners.get(i);
				lis.dispatchEnvironmentEvent(ev);
			}
		}
	}
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject ret = (ISpaceObject)spaceobjects.get(id);
			if(ret==null)
				throw new RuntimeException("Space object not found: "+id);
			return ret;
		}
	}
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject0(Object id)
	{
		synchronized(monitor)
		{
			return (ISpaceObject)spaceobjects.get(id);
		}
	}
	
	/**
	 * Get all space object of a specific type.
	 * @param type The space object type.
	 * @return The space objects of the desired type.
	 */
	public ISpaceObject[] getSpaceObjectsByType(Object type)
	{
		List obs = (List)spaceobjectsbytype.get(type);
		return obs==null? new ISpaceObject[0]: (ISpaceObject[])obs.toArray(new ISpaceObject[obs.size()]); 
	}
	

	/**
	 * Adds an avatar mapping.
	 * @param agenttype The agent type.
	 * @param objecttype The object type to represent the agent.
	 */
	public void addAvatarMappings(String agenttype, String objecttype)
	{
		synchronized(monitor)
		{
			this.avatarmappings.put(agenttype, objecttype);			
		}
	}

	/**
	 * Remove an avatar mapping.
	 * @param agenttype The agent type.
	 * @param objecttype The object type to represent the agent.
	 */
	public void removeAvatarMappings(String agenttype, String objecttype)
	{
		synchronized(monitor)
		{
			this.avatarmappings.remove(agenttype, objecttype);			
		}
	}

	/**
	 * Adds a space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addSpaceAction(Object id, ISpaceAction action)
	{
		synchronized(monitor)
		{
			spaceactions.put(id, action);
		}
	}
	
	/**
	 * Removes a space action.
	 * @param id the action ID
	 */
	public void removeSpaceAction(final Object id)
	{
		synchronized(monitor)
		{	
			spaceactions.remove(id);
		}
	}
	
	/**
	 * Performs an environment action.
	 * @param id ID of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performSpaceAction(final Object id, final Map parameters)
	{
		synchronized(monitor)
		{
			ISpaceAction action = (ISpaceAction) spaceactions.get(id);
			assert action != null;
			return action.perform(parameters, this);
		}
	}
	
	/**
	 * Adds an agent action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addAgentAction(Object id, IAgentAction action)
	{
		synchronized(monitor)
		{
			agentactions.put(id, action);
		}
	}
	
	/**
	 * Adds an agent action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	protected IAgentAction	getAgentAction(Object id)
	{
		IAgentAction	ret	= (IAgentAction)agentactions.get(id);
		if(ret==null)
		{
			throw new RuntimeException("No such agent action: "+id);
		}
		return ret;
	}

	/**
	 * Removes an agent action.
	 * @param actionId the action ID
	 */
	public void removeAgentAction(Object id)
	{
		synchronized(monitor)
		{	
			agentactions.remove(id);
		}
	}
	
	/**
	 * Schedules an agent action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public void performAgentAction(Object id, Map parameters, IResultListener listener)
	{
		synchronized(monitor)
		{
			actionlist.scheduleAgentAction(getAgentAction(id), parameters, listener);
		}
	}
	
	/**
	 *  Create a percept for the given agent.
	 *  @param type	The percept type.
	 *  @param data	The content of the percept (if any).
	 *  @param agent	The agent that should receive the percept.
	 */
	public void createPercept(String type, Object data, IAgentIdentifier agent)
	{
		synchronized(monitor)
		{
//			System.out.println("New percept: "+type+", "+data+", "+agent);
			
			String	agenttype	= ((ApplicationContext)getContext()).getAgentType(agent);
			IPerceptProcessor	proc	= (IPerceptProcessor)perceptmappings.get(agenttype);
			if(proc!=null)
				perceptlist.schedulePercept(type, data, agent, proc);
			else
				System.out.println("Warning: No processor for percept: "+type+", "+data+", "+agent);
		}
	}

	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public String getName()
	{
		synchronized(monitor)
		{
			return (String)getProperty("name");
		}
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public void setName(final String name)
	{
		synchronized(monitor)
		{
			setProperty("name", name);
		}
	}
	
	/**
	 *  Get the owner of an object.
	 *  @param id The id.
	 *  @return The owner.
	 */
	public IAgentIdentifier	getOwner(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			return (IAgentIdentifier)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
		}
	}
	
	/**
	 *  Set the owner of an object.
	 *  @param id The object id.
	 *  @param pos The object owner.
	 */
	public void setOwner(Object id, IAgentIdentifier owner)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			Object oldowner = obj.getProperty(ISpaceObject.PROPERTY_OWNER);
			if(oldowner!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(oldowner);
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(oldowner);
			}
			if(owner!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(owner);
				if(ownedobjs==null)
				{
					ownedobjs = new ArrayList();
					spaceobjectsbyowner.put(owner, ownedobjs);
				}
				ownedobjs.add(obj);
			}
			obj.setProperty(ISpaceObject.PROPERTY_OWNER, owner);
		}
	}
	
	/**
	 *  Get the owned objects.
	 *  @return The owned objects. 
	 */
	public ISpaceObject[] getOwnedObjects(IAgentIdentifier owner)
	{
		synchronized(monitor)
		{
			List ownedobjs = (List)spaceobjectsbyowner.get(owner);
			return ownedobjs==null? new ISpaceObject[0]: (ISpaceObject[])ownedobjs.toArray(new ISpaceObject[ownedobjs.size()]);
		}
	}
	
	/**
	 * Adds a view to the space.
	 * @param name name of the view
	 * @param view the view
	 */
	public void addDataView(String name, IDataView view)
	{
		synchronized (monitor)
		{
			views.put(name, view);
		}
	}
	
	/**
	 * Removes a view from the space.
	 * @param name name of the view
	 */
	public void removeDataView(String name)
	{
		synchronized (monitor)
		{
			views.remove(name);
		}
	}
	
	/**
	 * Gets a specific view.
	 * @param name name of the view
	 * @return the view
	 */
	public IDataView getDataView(String name)
	{
		synchronized (monitor)
		{
			return (IDataView) views.get(name);
		}
	}
	
	/**
	 * Get all available dataviews in this space.
	 * @return all available dataviews
	 */
	public Map getDataViews()
	{
		synchronized (monitor)
		{
			return new HashMap(views);
		}
	}
	
	/**
	 *  Add a mapping from object type to data view
	 *  @param objecttype	The object type.
	 *  @param view	Settings for view creation.
	 */
	public void addDataViewMapping(String objecttype, Map view)
	{
		synchronized(monitor)
		{
			dataviewmappings.put(objecttype, view);
		}
	}

	/**
	 *  Add an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void addEnvironmentListener(IEnvironmentListener listener)
	{
		synchronized(monitor)
		{
			if(listeners==null)
				listeners = new ArrayList();
			listeners.add(listener);
		}
	}
	
	/**
	 *  Remove an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void removeEnvironmentListener(IEnvironmentListener listener)
	{
		synchronized(monitor)
		{
			listeners.remove(listener);
			if(listeners.size()==0)
				listeners = null;
		}
	}
	
	/**
	 * Adds a percept generator.
	 * @param id The percept generator id.
	 * @param gen The percept generator.
	 */
	public void addPerceptGenerator(Object id, IPerceptGenerator gen)
	{
		synchronized(monitor)
		{
			addEnvironmentListener(gen);
			perceptgenerators.put(id, gen);
		}
	}
	
	/**
	 * Remove a percept generator.
	 * @param id The percept generator id.
	 */
	public void removePerceptGenerator(Object id)
	{
		synchronized(monitor)
		{
			removeEnvironmentListener((IEnvironmentListener)perceptgenerators.remove(id));
		}
	}

	/**
	 *  Add a percept mapping.
	 *  @param	agenttype	The agent type.
	 *  @param	proc	The percept processor.
	 */
	// Todo: multiple processors per agent -> mapping per percept type.
	public void addPerceptMapping(String agenttype, IPerceptProcessor proc)
	{
		synchronized(monitor)
		{
			perceptmappings.put(agenttype, proc);
		}
	}
	
	/**
	 *  remove a percept mapping.
	 *  @param	agenttype	The agent type.
	 *  @param	proc	The percept processor.
	 */
	// Todo: multiple processors per agent -> mapping per percept type.
	public void removePerceptMapping(String agenttype, IPerceptProcessor proc)
	{
		synchronized(monitor)
		{
			perceptmappings.remove(agenttype);
		}
	}
	
	//-------- ISpace methods --------
	
	/**
	 *  Called when an agent was added. 
	 */
	public void agentAdded(IAgentIdentifier aid)
	{
		synchronized(monitor)
		{
			// Add avatar(s) if any.
			String	agenttype	= ((ApplicationContext)getContext()).getAgentType(aid);
			if(agenttype!=null && avatarmappings.getCollection(agenttype)!=null)
			{
				for(Iterator it=avatarmappings.getCollection(agenttype).iterator(); it.hasNext(); )
				{
					String	objecttype	= (String)it.next();
					// Hmm local name as owner? better would be agent id, but agents are created after space?
					Map	props	= new HashMap();
					props.put(ISpaceObject.PROPERTY_OWNER, aid);
					createSpaceObject(objecttype, props, null, null);
				}
			}
			
			if(perceptgenerators!=null)
			{
				for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
				{
					IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
					gen.agentAdded(aid, this);
				}
			}
		}
	}
	
	/**
	 *  Called when an agent was removed.
	 */
	public void agentRemoved(IAgentIdentifier aid)
	{
		synchronized(monitor)
		{
			if(perceptgenerators!=null)
			{
				for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
				{
					IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
					gen.agentRemoved(aid, this);
				}
			}
		}
		
		// Remove the owned object too?
	}
	
	
	/**
	 *  Get the context.
	 *  @return The context.
	 */
	public IContext getContext()
	{
		return context;
	}
	
	/**
	 *  Set the context.
	 *  @param context The context.
	 */
	public void setContext(IContext context)
	{
		this.context = context;
	}

	/**
	 *  Get the space objects.
	 */
	// Hack!!! getSpaceObjecs() implemented in Space2D???
	protected Collection	getSpaceObjectsCollection()
	{
		return spaceobjects.values();
	}
	
	/**
	 *  Get the processes.
	 */
	protected Collection	getProcesses()
	{
		return processes.values();
	}
	
	/**
	 *  Get the list of scheduled agent actions
	 */
	public AgentActionList	getAgentActionList()
	{
		return actionlist;
	}
	
	/**
	 *  Get the list of scheduled percepts.
	 */
	protected PerceptList	getPerceptList()
	{
		return perceptlist;
	}
	
	/**
	 *  Get the views.
	 */
	protected Collection	getViews()
	{
		return views.values();
	}
	
	/** 
	 * Steps the space. May be non-functional in spaces that do not have
	 * a concept of steps.
	 * @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 * /
	public void step(IVector1 progress)
	{
		synchronized(monitor)
		{
			// Update the environment objects.
			for(Iterator it = spaceobjects.values().iterator(); it.hasNext(); )
			{
				SpaceObject obj = (SpaceObject)it.next();
				obj.updateObject(progress);
			}
			
			// Execute the scheduled agent actions.
			actionexecutor.executeEntries(null); // todo: where to get filter
			
			// Execute the processes.
			Object[] procs = processes.values().toArray();
			for(int i = 0; i < procs.length; ++i)
			{
				ISpaceProcess process = (ISpaceProcess) procs[i];
				process.execute(progress, this);
			}
			
			// Update the views.
			for (Iterator it = views.values().iterator(); it.hasNext(); )
			{
				IView view = (IView) it.next();
				view.update(this);
			}
		}
	}*/
	
	
	/**
	 *  Fire an environment event.
	 *  @param event The event.
	 */
	protected void fireEnvironmentEvent(EnvironmentEvent event)
	{
		synchronized(monitor)
		{
			if(listeners!=null)
			{
				for(int i=0; i<listeners.size(); i++)
				{
					((IEnvironmentListener)listeners.get(i)).dispatchEnvironmentEvent(event);
				}
			}
		}
	}
	
	/**
	 *  Synchronized counter class
	 */
	private class AtomicCounter
	{
		long count_;
		
		public AtomicCounter()
		{
			count_ = 0;
		}
		
		public synchronized Long getNext()
		{
			return new Long(count_++);
		}
	}
}
