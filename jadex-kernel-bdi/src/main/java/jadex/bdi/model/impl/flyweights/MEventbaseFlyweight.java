package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMEventbase;
import jadex.bdi.model.IMInternalEvent;
import jadex.bdi.model.IMInternalEventReference;
import jadex.bdi.model.IMMessageEvent;
import jadex.bdi.model.IMMessageEventReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for the event base model.
 */
public class MEventbaseFlyweight extends MElementFlyweight implements IMEventbase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public MEventbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods --------
	
	/**
	 *  Get an internal event for a name.
	 *  @param name	The event name.
	 */
	public IMInternalEvent getInternalEvent(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents, name);
					if(handle==null)
						throw new RuntimeException("Event not found: "+name);
					object = new MInternalEventFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMInternalEvent)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents, name);
			if(handle==null)
				throw new RuntimeException("Event not found: "+name);
			return new MInternalEventFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Get a message event for a name.
	 *  @param name	The event set name.
	 */
	public IMMessageEvent getMessageEvent(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents, name);
					if(handle==null)
						throw new RuntimeException("Event not found: "+name);
					object = new MInternalEventFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMMessageEvent)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents, name);
			if(handle==null)
				throw new RuntimeException("Event not found: "+name);
			return new MMessageEventFlyweight(getState(), getScope(), handle);
		}
	}
	
	/**
	 *  Returns all internal events.
	 *  @return All internal events.
	 */
	public IMInternalEvent[] getInternalEvents()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents);
					IMInternalEvent[] ret = new IMInternalEvent[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MInternalEventFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMInternalEvent[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents);
			IMInternalEvent[] ret = new IMInternalEvent[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MInternalEventFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}

	/**
	 *  Return all message events.
	 *  @return All message events.
	 */
	public IMMessageEvent[] getMessageEvents()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents);
					IMMessageEvent[] ret = new IMMessageEvent[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MMessageEventFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMMessageEvent[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents);
			IMMessageEvent[] ret = new IMMessageEvent[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MMessageEventFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get an internal event reference for a name.
	 *  @param name	The event name.
	 */
	public IMInternalEventReference getInternalEventReference(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internaleventrefs, name);
					if(handle==null)
						throw new RuntimeException("InternalEvent reference not found: "+name);
					object = new MInternalEventReferenceFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMInternalEventReference)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internaleventrefs, name);
			if(handle==null)
				throw new RuntimeException("InternalEvent reference not found: "+name);
			return new MInternalEventReferenceFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all internal event references.
	 *  @return All internal event references.
	 */
	public IMInternalEventReference[] getInternalEventReferences()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internaleventrefs);
					IMInternalEventReference[] ret = new IMInternalEventReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MInternalEventReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMInternalEventReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internaleventrefs);
			IMInternalEventReference[] ret = new IMInternalEventReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MInternalEventReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get a message event reference for a name.
	 *  @param name	The event set name.
	 */
	public IMMessageEventReference getMessageEventReference(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageeventrefs, name);
					if(handle==null)
						throw new RuntimeException("MessageEvent reference not found: "+name);
					object = new MMessageEventReferenceFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMMessageEventReference)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageeventrefs, name);
			if(handle==null)
				throw new RuntimeException("MessageEvent reference not found: "+name);
			return new MMessageEventReferenceFlyweight(getState(), getScope(), handle);
		}
	}
	
	/**
	 *  Return all message event references.
	 *  @return All message event references.
	 */
	public IMMessageEventReference[] getMessageEventReferences()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageeventrefs);
					IMMessageEventReference[] ret = new IMMessageEventReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MMessageEventReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMMessageEventReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageeventrefs);
			IMMessageEventReference[] ret = new IMMessageEventReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MMessageEventReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
}
