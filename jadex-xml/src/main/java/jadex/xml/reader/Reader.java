package jadex.xml.reader;

import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IPostProcessor;
import jadex.xml.SXML;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

import jadex.xml.stax.QName;
import jadex.xml.stax.StaxLocationWrapper;
import jadex.xml.stax.StaxXMLReporterWrapper;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *  Stax XML reader.
 */
public class Reader extends AReader
{
	//-------- attributes --------
	
//	/** The type info manager. */
//	protected TypeInfoPathManager tipmanager;
	
//	/** The default object reader handler (if any). */
//	protected IObjectReaderHandler defaulthandler;
	
	/** The link mode. */
	protected boolean bulklink;
	
	/** The xml input factory. */
	protected XMLInputFactory	factory;
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param readerhandler The handler.
	 */
	public Reader()
	{
		this(false);
	}
	
	/**
	 *  Create a new reader.
	 */
	public Reader(boolean bulklink)
	{
		this(bulklink, false, null);
	}
	
	/**
	 *  Create a new reader.
	 */
	public Reader(boolean bulklink, boolean validate, XMLReporter reporter)
	{
		// Xerces has a stackoverflow bug when coalescing is set to true :-(
		this(bulklink, validate, false, reporter);
	}
	
	/**
	 *  Create a new reader.
	 *  @param readerhandler The handler.
	 */
	public Reader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter)
	{
		this.bulklink = bulklink;
		factory	= XMLInputFactory.newInstance();
		
		try
		{
			factory.setProperty(XMLInputFactory.IS_VALIDATING, validate ? Boolean.TRUE : Boolean.FALSE);
		}
		catch(Exception e)
		{
			// Validation not supported.
			System.err.println("Error setting validation to "+validate);
//			e.printStackTrace();
		}
		
		try
		{
			factory.setProperty(XMLInputFactory.IS_COALESCING, coalescing ? Boolean.TRUE : Boolean.FALSE);
		}
		catch(Exception e)
		{
			// Validation not supported.
			System.err.println("Error setting coalescing to "+coalescing);
//			e.printStackTrace();
		}
		
		if(reporter!=null)
		{
			factory.setProperty(XMLInputFactory.REPORTER, reporter);
		}
		else
		{
			factory.setProperty(XMLInputFactory.REPORTER, new XMLReporter()
			{
				public void report(String message, String error, Object related, Location location)	throws XMLStreamException
				{
					throw new XMLStreamException(message, location);
				}
			});			
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
	 */
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, java.io.Reader input, final ClassLoader classloader, final Object callcontext) throws Exception
	{
		XMLStreamReader	parser;
		synchronized(factory)
		{
			parser	= factory.createXMLStreamReader(input);
		}
		return read(tipmanager, handler, parser, classloader, callcontext);
	}
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
	 */
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, InputStream input, final ClassLoader classloader, final Object callcontext) throws Exception
	{
		XMLStreamReader	parser;
		synchronized(factory)
		{
			parser	= factory.createXMLStreamReader(input);
		}
		return read(tipmanager, handler, parser, classloader, callcontext);
	}

	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, XMLStreamReader parser, final ClassLoader classloader, final Object callcontext) throws Exception
	{
		ReadContextDesktop readcontext = new ReadContextDesktop(tipmanager, handler, parser, StaxXMLReporterWrapper.fromXMLReporter(factory.getXMLReporter()), callcontext, classloader);
		READ_CONTEXT.set(readcontext);
		try
		{
			while(parser.hasNext())
			{
				int	next = parser.next();
				
				if(next==XMLStreamReader.COMMENT)
				{
					handleComment(readcontext);
				}
				else if(next==XMLStreamReader.CHARACTERS || next==XMLStreamReader.CDATA)
				{
					handleContent(readcontext);
				}
				else if(next==XMLStreamReader.START_ELEMENT)
				{	
					handleStartElement(readcontext);
				}
				else if(next==XMLStreamReader.END_ELEMENT)
				{
					handleEndElement(readcontext);
				}
			}
			
			// Handle post-processors.
			for(int i=1; readcontext.getPostProcessors().size()>0; i++)
			{
				List ps = (List)readcontext.getPostProcessors().remove(Integer.valueOf(i));
				if(ps!=null)
				{
					for(int j=0; j<ps.size(); j++)
					{
						((IPostProcessorCall)ps.get(j)).callPostProcessor();
					}
				}
	//			System.out.println("i: "+i);
			}
		}
		catch(RuntimeException e)
		{
//			e.printStackTrace();
			jadex.xml.stax.ILocation	loc	= readcontext.getStackSize()>0 ? readcontext.getTopStackElement().getLocation() : StaxLocationWrapper.fromLocation(parser.getLocation());
			readcontext.getReporter().report(e.toString(), "XML error", readcontext, loc);
		}
		finally
		{
			READ_CONTEXT.set(null);
			parser.close();
		}

		return readcontext.getRootObject()==NULL ? null : readcontext.getRootObject();
	}
	
	/**
	 *  Handle the comment.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleComment(ReadContextDesktop readcontext) throws Exception
	{
		if(readcontext.getReadIgnore()==0)
			readcontext.setComment(readcontext.getParser().getText());
		else if(DEBUG)
			System.out.println("Ignoring: "+readcontext.getParser().getText());
		//	System.out.println("Found comment: "+comment);
	}

	/**
	 *  Handle the content.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleContent(ReadContextDesktop readcontext) throws Exception
	{
		if(readcontext.getReadIgnore()==0)
			readcontext.getTopStackElement().addContent(readcontext.getParser().getText()); 
		else if(DEBUG)
			System.out.println("Ignoring: "+readcontext.getParser().getText());
//		System.out.println("content: "+parser.getLocalName()+" "+content);
	}
	
	// For debugging: ReadContext -> Integer.
//	private static Map	stackdepth	= Collections.synchronizedMap(new HashMap());
	
	/**
	 *  Handle the start element.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleStartElement(ReadContextDesktop readcontext) throws Exception
	{
		XMLStreamReader parser = readcontext.getParser();
		
		if(readcontext.getReadIgnore()>0)
		{
			readcontext.setReadIgnore(readcontext.getReadIgnore()+1);
			if(DEBUG)
				System.out.println("Ignoring: "+parser.getLocalName());
		}
		else if(readcontext.getReadIgnore()==0)
		{
//			List stack = readcontext.getStack();

			// Fetch for info when creating attributes.
			Map rawattrs = null;
			int attrcnt = parser.getAttributeCount();
			if(attrcnt>0)
			{
				rawattrs = new HashMap();
				for(int i=0; i<attrcnt; i++)
				{
					String attrname = parser.getAttributeLocalName(i);
					String attrval = parser.getAttributeValue(i);
					
					rawattrs.put(attrname, attrval);
				}
			}
			
			Object object = null;
			
//			System.out.println("here: "+parser.getPrefix()+" "+parser.getNamespaceURI()+" "+parser.getLocalName()+" "+parser.getName());
			jadex.xml.stax.QName localname = convertStaXQNameToQName(parser.getName());
			
//			QName localname = parser.getPrefix()==null || parser.getPrefix()==XMLConstants.DEFAULT_NS_PREFIX? new QName(parser.getLocalName())
//				: new QName(parser.getNamespaceURI(), parser.getLocalName(), parser.getPrefix());
			
			jadex.xml.stax.QName[] fullpath = readcontext.getXMLPath(localname);
			
			// Get type info and corresponding handler.
			TypeInfo typeinfo = readcontext.getPathManager().getTypeInfo(localname, fullpath, rawattrs);
			IObjectReaderHandler	handler	= typeinfo!=null ? typeinfo.getReaderHandler() : null;
			if(handler==null)
			{
				if(readcontext.getTopStackElement()!=null && readcontext.getTopStackElement().getReaderHandler()!=null)
				{
					handler	= readcontext.getTopStackElement().getReaderHandler();
				}
				else if(readcontext.getDefaultHandler()!=null)
				{
					handler	= readcontext.getDefaultHandler();
				}
				else
				{
					readcontext.getReporter().report("No handler for element: "+localname, "type info error", readcontext, StaxLocationWrapper.fromLocation(parser.getLocation()));
				}
			}
			
			// Find out if we need to ignore. 
			if(readcontext.getStackSize()>0)
			{
				StackElement pse = (StackElement)readcontext.getTopStackElement();
				List pathname = new ArrayList();
				pathname.add(localname);
				for(int i=readcontext.getStackSize()-2; i>=0 && pse.getObject()==null; i--)
				{
					pse = (StackElement)readcontext.getStackElement(i);
					pathname.add(0, readcontext.getStackElement(i+1).getTag());
				}
				
				if(pse!=null)
				{
					TypeInfo patypeinfo = ((StackElement)pse).getTypeInfo();
					SubobjectInfo linkinfo = getSubobjectInfoRead(localname, fullpath, patypeinfo, 
						readcontext.getTopStackElement()!=null? readcontext.getTopStackElement().getRawAttributes(): null);
					if(linkinfo!=null && linkinfo.getAccessInfo().isIgnoreRead())
					{
						readcontext.setReadIgnore(readcontext.getReadIgnore()+1);
						if(DEBUG)
							System.out.println("Ignoring: "+parser.getLocalName());
					}
				}
			}
			
			if(readcontext.getReadIgnore()==0)
			{
				// Test if it is an object reference
				String idref = rawattrs!=null? (String)rawattrs.get(SXML.IDREF): null;
				if(idref!=null)
				{
					if(readcontext.getReadObjects().containsKey(idref))
					{
						object = readcontext.getReadObjects().get(idref);
						StackElement se = new StackElement(handler, localname, object, rawattrs, typeinfo, StaxLocationWrapper.fromLocation(parser.getLocation()));
						readcontext.addStackElement(se);
					}
					else
					{
						StackElement se = new StackElement(handler, localname, null, rawattrs, typeinfo, StaxLocationWrapper.fromLocation(parser.getLocation()));
						readcontext.addStackElement(se);
						readcontext.getReporter().report("idref not contained: "+idref, "idref error", se, se.getLocation());						
					}
				}
				else
				{	
					// Create object.
					// todo: do not call createObject on every tag?!
					Object ti = typeinfo;
					if(localname.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO)
						&& (typeinfo==null || typeinfo.isCreateFromTag()))
					{
						ti = localname;
					}
					
					try
					{
						object = handler.createObject(ti, readcontext.getStackSize()==0, readcontext, rawattrs);
					}
					catch(Exception e)
					{
//						e.printStackTrace();
						readcontext.getReporter().report(e.toString(), "creation error", readcontext, StaxLocationWrapper.fromLocation(parser.getLocation()));
						readcontext.removeStackElement();	// remove ignored element info from stack. otherwise parser would be confused.
						readcontext.setReadIgnore(1);
					}
					
					if(DEBUG && object==null)
						System.out.println("No mapping found: "+localname);
					
					// Try to search type info via type (when tag contained type information)
					if(typeinfo==null && object!=null)
					{
						typeinfo = handler.getTypeInfo(object, fullpath, readcontext);
					}
					
					// If object has internal id save it in the readobjects map.
					String id = rawattrs!=null? (String)rawattrs.get(SXML.ID): null;
					if(id!=null && object!=null)
					{
//						System.out.println("ID: "+id+", "+object.getClass());
						readcontext.getReadObjects().put(id, object);
					}
					
					readcontext.addStackElement(new StackElement(handler, localname, object, rawattrs, typeinfo, StaxLocationWrapper.fromLocation(parser.getLocation())));
				
					// Handle attributes.
					int atcnt = attrcnt;
					if(rawattrs!=null)
					{
						if(rawattrs.containsKey(SXML.ID))
							atcnt--;
						if(rawattrs.containsKey(SXML.ARRAYLEN))
							atcnt--;
					}
					if(atcnt>0)
					{
						List<QName>	attrpath	= null;
						// If no type use last element from stack to map attributes.
						if(object==null)	
						{
							attrpath = new ArrayList<QName>();
							attrpath.add(readcontext.getTopStackElement().getTag());
							for(int i=readcontext.getStackSize()-2; i>=0 && object==null; i--)
							{
								StackElement pse = readcontext.getStackElement(i);
								attrpath.add(pse.getTag());
								typeinfo = pse.getTypeInfo();
								object = pse.getObject();
							}
						}
						
						// Handle attributes
						if(object!=null)
						{
							Set attrs = typeinfo==null? Collections.EMPTY_SET: typeinfo.getXMLAttributeNames();
							for(int i=0; i<parser.getAttributeCount(); i++)
							{
								QName attrname = parser.getAttributePrefix(i)==null || XMLConstants.DEFAULT_NS_PREFIX.equals(parser.getAttributePrefix(i))? new QName(parser.getAttributeLocalName(i))
									: new QName(parser.getAttributeNamespace(i), parser.getAttributeLocalName(i), parser.getAttributePrefix(i));
	
	//							System.out.println("here: "+attrname);
								
								if(!SXML.ID.equals(attrname.getLocalPart()) && !SXML.ARRAYLEN.equals(attrname.getLocalPart()))
								{	
									String attrval = parser.getAttributeValue(i);
									attrs.remove(attrname);
									
									Object attrinfo = typeinfo!=null ? typeinfo.getAttributeInfo(attrname) : null;
									
									// Try finding attr info by path.
									if(attrinfo==null && typeinfo!=null && attrpath!=null)
									{
										List<QName>	key	= new LinkedList<QName>();
										key.add(attrname);
										for(int j=0; attrinfo==null && j<attrpath.size(); j++)
										{
											key.add(0, attrpath.get(j));
											attrinfo	= typeinfo.getAttributeInfo(new Tuple(key.toArray()));
										}
									}
									
									if(!(attrinfo instanceof AttributeInfo && ((AttributeInfo)attrinfo).isIgnoreRead()))
									{
			//							ITypeConverter attrconverter = typeinfo!=null ? typeinfo.getAttributeConverter(attrname) : null;
			//							Object val = attrconverter!=null? attrconverter.convertObject(attrval, root, classloader): attrval;
										
										handler.handleAttributeValue(object, attrname, attrpath, attrval, attrinfo, readcontext);
									
										if(attrinfo instanceof AttributeInfo && AttributeInfo.ID.equals(((AttributeInfo)attrinfo).getId()))
										{
	//										System.out.println("ID: "+attrval+", "+object);
											readcontext.getReadObjects().put(attrval, object);
										}
									}
								}
							}
							// Handle unset attributes (possibly have default value).
							for(Iterator it=attrs.iterator(); it.hasNext(); )
							{
								Object key = it.next();
								if(key instanceof QName)	// ignore path entries of same attributes.
								{
									Object attrinfo = typeinfo.getAttributeInfo(key);
									
									// Hack. want to read attribute info here
									handler.handleAttributeValue(object, (QName)key , attrpath, null, attrinfo, readcontext);
								}
							}
						}
						else
						{
							StackElement	se	= readcontext.getTopStackElement();
							readcontext.getReporter().report("No element on stack for attributes", "stack error", se, se.getLocation());													
						}
					}
					
					// Handle comment.
					if(readcontext.getComment()!=null && typeinfo!=null)
					{
						Object commentinfo = typeinfo.getCommentInfo();
						if(commentinfo!=null)
						{
							handler.handleAttributeValue(object, null, null, readcontext.getComment(), commentinfo, 
								readcontext);
						}
					}
				}
			}
			
			readcontext.setComment(null);
			
//			System.out.println("start: "+parser.getLocalName());
		}
	}
	
	/**
	 *  Handle the end element.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleEndElement(final ReadContextDesktop readcontext) throws Exception
	{
		if(readcontext.getReadIgnore()==0)
		{
			XMLStreamReader parser = readcontext.getParser();
//			List stack = readcontext.getStack();
			StackElement topse = readcontext.getTopStackElement();
			
	//		System.out.println("end: "+parser.getLocalName());
//			QName localname = parser.getPrefix()==null || parser.getPrefix()==XMLConstants.DEFAULT_NS_PREFIX? new QName(parser.getLocalName())
//				: new QName(parser.getNamespaceURI(), parser.getLocalName(), parser.getPrefix());
			
			jadex.xml.stax.QName localname = convertStaXQNameToQName(parser.getName());
			jadex.xml.stax.QName[] fullpath = readcontext.getXMLPath();
			final TypeInfo typeinfo = readcontext.getPathManager().getTypeInfo(localname, fullpath, topse.getRawAttributes());
	
			// Hack. Change object to content when it is element of its own.
			if((topse.getObject()==null && topse.getContent()!=null && topse.getContent().trim().length()>0) || topse.getObject()==STRING_MARKER)
			{
				// Handle possible content type conversion.
				Object val = topse.getContent()!=null? topse.getContent(): topse.getObject();
				if(val instanceof String)
				{
					if(typeinfo!=null && typeinfo.getContentInfo()!=null)
					{
						Object coninfo = typeinfo.getContentInfo();
						if(coninfo!=null && coninfo instanceof AttributeInfo)
						{
							IStringObjectConverter conv = ((AttributeInfo)coninfo).getConverter();
							if(conv!=null)
							{
								val = conv.convertString((String)val, readcontext);
							}
						}
					}
					else
					{
						val = topse.getReaderHandler().convertContentObject((String)val, localname, readcontext);
					}
				}
				
				topse = new StackElement(topse.getReaderHandler(), topse.getTag(), val, topse.getRawAttributes(), null, topse.getLocation());
				readcontext.setStackElement(topse, readcontext.getStackSize()-1);
//				stack.set(stack.size()-1, topse);
//				readcontext.setTopse(topse);
			}
			
			// Handle content.
			if(topse.getObject()!=null && topse.getContent()!=null && topse.getContent().trim().length()>0)
			{
				if(typeinfo!=null && typeinfo.getContentInfo()!=null) 
				{
					topse.getReaderHandler().handleAttributeValue(topse.getObject(), null, null, topse.getContent(), typeinfo.getContentInfo(), readcontext);
				}
				else
				{
					StackElement	se	= readcontext.getTopStackElement();
					readcontext.getReporter().report("No content mapping for: "+topse.getContent()+" tag="+topse.getTag(), "link error", se, se.getLocation());													
				}
			}
				
			// Handle post-processing			
			final IPostProcessor[] postprocs = topse.getReaderHandler().getPostProcessors(topse.getObject(), typeinfo);
			if(postprocs!=null && postprocs.length>0)
			{
				for(int i=0; i<postprocs.length; i++)
				{
					if(postprocs[i].getPass()==0)
					{
						try
						{
							Object changed = postprocs[i].postProcess(readcontext, topse.getObject());
							
							if(changed == IPostProcessor.DISCARD_OBJECT)
							{
								topse.setObject(null);
							}
							else if(changed!=null)
							{
								topse.setObject(changed);
							}
						}
						catch(RuntimeException e)
						{
//							e.printStackTrace();
							readcontext.getReporter().report("Error during postprocessing: "+e, "postprocessor error", topse, topse.getLocation());																				
						}
					}
					else
					{
						final StackElement ftopse = topse;
						final StackElement[] stack = readcontext.getStack();	// Use snapshot of stack for error report, as stack isn't available in delayed post processors.
						final int fi = i;
						readcontext.getPostProcessors().add(Integer.valueOf(postprocs[i].getPass()), new IPostProcessorCall()
						{
							public void callPostProcessor() throws Exception
							{
								try
								{
									Object check = postprocs[fi].postProcess(readcontext, ftopse.getObject());
									if(check!=null)
									{
										readcontext.getReporter().report("Object replacement only possible in first pass.", "postprocessor error", ftopse, ftopse.getLocation());																				
									}
								}
								catch(Exception e)
								{
//									e.printStackTrace();
									readcontext.getReporter().report("Error during postprocessing: "+e, "postprocessor error", stack, ftopse!=null ? ftopse.getLocation() : readcontext.getLocation());																				
								}
							}
						});
					}
				}
			}
			
			// If object has internal id save it in the readobjects map.
			String id = topse.getRawAttributes()!=null? (String)topse.getRawAttributes().get(SXML.ID): null;
			if(id!=null && topse.getObject()!=null)
			{
//				System.out.println("ID: "+id+", "+val.getClass());
				readcontext.getReadObjects().put(id, topse.getObject());
			}				
	
			// Link current object to parent
			if(topse.getObject()!=null)
			{					
				// Handle linking
				boolean bulklink = typeinfo!=null? typeinfo.isBulkLink(): this.bulklink;
				if(readcontext.getStackSize()>0 && bulklink)
				{
					// Invoke bulk link for the finished object (as parent).
					List childs = readcontext.removeChildren(topse.getObject());
					if(childs!=null)
					{
						IBulkObjectLinker linker = (IBulkObjectLinker)(typeinfo!=null && typeinfo.getLinker()!=null? typeinfo.getLinker(): topse.getReaderHandler());
						linker.bulkLinkObjects(topse.getObject(), childs, readcontext);
					}
				}
				if(readcontext.getStackSize()>1)
				{
					StackElement pse = readcontext.getStackElement(readcontext.getStackSize()-2);
					ArrayList<jadex.xml.stax.QName> pathname = new ArrayList<jadex.xml.stax.QName>();
					pathname.add(localname);
					for(int i=readcontext.getStackSize()-3; i>=0 && pse.getObject()==null; i--)
					{
						pse = readcontext.getStackElement(i);
						pathname.add(0, readcontext.getStackElement(i+1).getTag());
					}
					
					if(pse.getObject()!=null)
					{
	//						System.out.println("here: "+parser.getLocalName()+" "+getXMLPath(stack)+" "+topse.getRawAttributes());
					
						TypeInfo patypeinfo = pse.getTypeInfo();
						SubobjectInfo linkinfo = getSubobjectInfoRead(localname, fullpath, patypeinfo, topse.getRawAttributes());
						bulklink = patypeinfo!=null? patypeinfo.isBulkLink(): this.bulklink;
						
						if(!bulklink)
						{
							IObjectLinker linker = (IObjectLinker)(patypeinfo!=null && patypeinfo.getLinker()!=null? patypeinfo.getLinker(): pse.getReaderHandler());
							linker.linkObject(topse.getObject(), pse.getObject(), linkinfo==null? null: linkinfo, 
								pathname.toArray(new jadex.xml.stax.QName[pathname.size()]), readcontext);
							
//							IObjectLinker linker = (IObjectLinker)(patypeinfo!=null && patypeinfo.getLinker()!=null? patypeinfo.getLinker(): null);
//							boolean linked = false;
//							if(linker!=null)
//							{
//								linked = linker.linkObject(topse.getObject(), pse.getObject(), linkinfo==null? null: linkinfo, 
//									pathname.toArray(new jadex.xml.stax.QName[pathname.size()]), readcontext);
//							}
//							
//							if(!linked)
//							{
//								linker = pse.getReaderHandler();
//								linker.linkObject(topse.getObject(), pse.getObject(), linkinfo==null? null: linkinfo, 
//									pathname.toArray(new jadex.xml.stax.QName[pathname.size()]), readcontext);
//							}
						}
						else
						{
							// Save the finished object as child for its parent.
							readcontext.addChild(pse.getObject(), new LinkData(topse.getObject(), linkinfo==null? null: linkinfo, 
								(jadex.xml.stax.QName[])pathname.toArray(new jadex.xml.stax.QName[pathname.size()])));	
						}
					}
					else
					{
						StackElement	se	= readcontext.getTopStackElement();
						readcontext.getReporter().report("No parent object found for: "+SUtil.arrayToString(fullpath), "link error", se, se.getLocation());													
					}
				}
			}
			
			readcontext.removeStackElement();
		}
		else
		{
			readcontext.setReadIgnore(readcontext.getReadIgnore()-1);
		}
	}
	
	/**
	 * Converts a StaX QName object to a jadex.xml.stax.qname object.
	 * @param qname
	 * @return QName
	 */
	private static QName convertStaXQNameToQName(javax.xml.namespace.QName qname) {
		return new QName(qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix());
	}
	
//	private static javax.xml.namespace.QName convertQNameToStaxQName(jadex.xml.wrapper.QName qname) {
//		return new javax.xml.namespace.QName(qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix());
//	}
	
//	private static javax.xml.namespace.QName[] convertQNameToStaxQName(jadex.xml.wrapper.QName[] qnames) {
//		javax.xml.namespace.QName[] result = new javax.xml.namespace.QName[qnames.length];
//		for (int i = 0; i < result.length; i++)
//		{
//			result[i] = convertQNameToStaxQName(qnames[i]);
//		}
//		return result;
//	}
//	
//	private static jadex.xml.wrapper.QName[] convertStaXQNameToQName(javax.xml.namespace.QName[] qnames) {
//		jadex.xml.wrapper.QName[] result = new jadex.xml.wrapper.QName[qnames.length];
//		for (int i = 0; i < result.length; i++)
//		{
//			result[i] = convertStaXQNameToQName(qnames[i]);
//		}
//		return result;
//	}
}
