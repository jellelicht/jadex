package jadex.bdiv3;

import jadex.android.commons.JadexDexClassLoader;
import jadex.android.commons.Logger;
import jadex.bdiv3.android.DexLoader;
import jadex.bdiv3.android.LogClassWriter;
import jadex.bdiv3.asmdex.ClassNodeWrapper;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.Type;
import org.ow2.asmdex.ApplicationReader;
import org.ow2.asmdex.ApplicationVisitor;
import org.ow2.asmdex.ApplicationWriter;
import org.ow2.asmdex.ClassVisitor;
import org.ow2.asmdex.MethodVisitor;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.tree.AbstractInsnNode;
import org.ow2.asmdex.tree.ApplicationNode;
import org.ow2.asmdex.tree.ClassNode;
import org.ow2.asmdex.tree.FieldInsnNode;
import org.ow2.asmdex.tree.FieldNode;
import org.ow2.asmdex.tree.InsnList;
import org.ow2.asmdex.tree.LabelNode;
import org.ow2.asmdex.tree.MethodInsnNode;
import org.ow2.asmdex.tree.MethodNode;
import org.ow2.asmdex.tree.VarInsnNode;

import android.util.Log;

public class AsmDexBdiClassGenerator extends AbstractAsmBdiClassGenerator
{
	protected static Method methoddc1;
	protected static Method methoddc2;
	public static File OUTPATH;

	static
	{
		try
		{
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
			{
				public Object run() throws Exception
				{
					Class<?> cl = Class.forName("java.lang.ClassLoader");
					methoddc1 = cl.getDeclaredMethod("defineClass", new Class[]
					{String.class, byte[].class, int.class, int.class});
					methoddc2 = cl.getDeclaredMethod("defineClass", new Class[]
					{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
					return null;
				}
			});
		}
		catch (PrivilegedActionException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> generateBDIClass(String classname, BDIModel micromodel, ClassLoader cl)
	{
		return generateBDIClass(classname, micromodel, cl, new HashSet<String>());
	}

	/**
	 * Generate class. Generated class should be available in the given
	 * classLoader at the end of this method.
	 */
	public Class<?> generateBDIClass(final String clname, final BDIModel model, final ClassLoader cl, final Set<String> done)
	{
		Class<?> ret = null;

		final List<String> todo = new ArrayList<String>();
		done.add(clname);

		int api = Opcodes.ASM4;
		final String iclname = clname.replace(".", "/");
		final String iname = "L" + clname.replace('.', '/') + ";";

		// pattern to accept all inner classes
		final Pattern classPattern = Pattern.compile("L" + clname.replace('.', '/') + ";?\\$?.*");

		try
		{
			JadexDexClassLoader androidCl = (JadexDexClassLoader) SUtil.androidUtils().findJadexDexClassLoader(cl.getParent());
			// is = SUtil.getResource(APP_PATH, cl);
			String appPath = ((JadexDexClassLoader) androidCl).getDexPath();
			InputStream is = getFileInputStream(new File(appPath));
			ApplicationReader ar = new ApplicationReader(api, is);
			ApplicationWriter aw = new ApplicationWriter();
			ApplicationNode an = new ApplicationNode(api);
			
			final ArrayList<ClassNode> classes = new ArrayList<ClassNode>();
			
			ApplicationVisitor av = new ApplicationVisitor(api, an)
			{

				@Override
				public ClassVisitor visitClass(int access, String name, String[] signature, String superName, String[] interfaces)
				{
					if (classPattern.matcher(name).matches())
					{
						System.out.println("visit class: " + name);
						final ClassNode cn = new ClassNode(api, access, name, signature, superName, interfaces);
						classes.add(cn);
						final ClassVisitor superVisitor = super.visitClass(access, iname, signature, superName, interfaces);
						ClassVisitor cv = new ClassVisitor(api, cn)
						{

							@Override
							public MethodVisitor visitMethod(int access, String name, String desc, String[] signature, String[] exceptions)
							{
								return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions))
								{

									@Override
									public void visitFieldInsn(int opcode, String owner, String name, String desc, int valueRegister,
											int objectRegister)
									{
										// objectRegister = reference to instance (ignored when static),
										// valueRegister = index of value to put!
										if (isInstancePutField(opcode) && model.getCapability().hasBelief(name)
												&& model.getCapability().getBelief(name).isFieldBelief())
										{
											//
											// possibly transform basic value
											if (SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
											{
												visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, "Ljadex/commons/SReflect;", "wrapValue",
														"Ljava/lang/Object;" + desc, new int[]
														{valueRegister});
												visitMethodInsn(Opcodes.INSN_MOVE_RESULT_OBJECT, null, null, null, new int[]{1});
												
												// log:
									 			visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "Vjava/lang/Object;", new int[]{1});
											}

											 // fetch bdi agent value from field
											//
											// // this pop aload is necessary in
											// inner
											// // classes!
											// visitInsn(Opcodes.POP);
											// visitVarInsn(Opcodes.ALOAD, 0);
											// super.visitFieldInsn(Opcodes.GETFIELD,
											// iclname, "__agent",
											// Type.getDescriptor(BDIAgent.class));
											
											super.visitFieldInsn(Opcodes.INSN_IGET_OBJECT, iname, "__agent", Type.getDescriptor(BDIAgent.class), 0, 2);
											
											// log:
											visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "Vjava/lang/Object;", new int[]{2});
											
											
											
											// // add field name
											// visitLdcInsn(name);
											// visitInsn(Opcodes.SWAP);
											// // add this
											// visitVarInsn(Opcodes.ALOAD, 0);
											// visitInsn(Opcodes.SWAP);
											//
											// // invoke method
											// visitMethodInsn(Opcodes.INVOKESTATIC,
											// "jadex/bdiv3/BDIAgent",
											// "writeField",
											// "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljadex/bdiv3/BDIAgent;)V");
										}
										else
										{
											super.visitFieldInsn(opcode, owner, name, desc, valueRegister, objectRegister);
										}
									}

								};
							}

							@Override
							public void visitInnerClass(String name, String outerName, String innerName, int access)
							{
								// System.out.println("vic: " + name + " " +
								// outerName + " " + innerName + " " + access);
								String icln = (name == null ? null : name.replace("/", "."));
								if (!done.contains(icln))
									todo.add(icln);
								super.visitInnerClass(name, outerName, innerName, access);// Opcodes.ACC_PUBLIC);
																							// does
																							// not
																							// work
							}

							@Override
							public void visitEnd()
							{
								visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(BDIAgent.class), null, null);
								visitField(Opcodes.ACC_PUBLIC, "__globalname", Type.getDescriptor(String.class), null, null);
								super.visitEnd();
							}

						};

						return cv;
					}
					else
					{
						return null;
					}
				}

			};

//			 ar.accept(aa, new String[]{iname}, 0);
			ar.accept(av, null, 0); // visit all classes
			
			aw.visit();
			for (ClassNode classNode : classes)
			{
				transformClassNode(classNode, iclname, model);
				String[] signature = classNode.signature == null? null : classNode.signature.toArray(new String[classNode.signature.size()]);
				String[] interfaces = classNode.interfaces == null ? null :classNode.interfaces.toArray(new String[classNode.interfaces.size()]);
				System.out.println("write class: " + classNode.name);
				ClassVisitor visitClass = aw.visitClass(classNode.access, classNode.name, signature, classNode.superName, interfaces);
				classNode.accept(visitClass);
			}
			aw.visitEnd();

			byte[] dex = aw.toByteArray();

			// we need the android user loader as parent here, because class
			// dependencies for the agent could exist
			ClassLoader newCl = DexLoader.load(androidCl, dex, OUTPATH);

			Class<?> generatedClass = newCl.loadClass(clname);
			ret = generatedClass;

			if (androidCl != null)
			{
				androidCl.defineClass(clname, generatedClass);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	private void transformClassNode(ClassNode cn, String iclname, BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its
		// inner classes.
		boolean agentclass = isAgentClass(ClassNodeWrapper.wrap(cn));

//		final String iclname = iname.replace(".", "/");

		// Check method for array store access of beliefs and replace with
		// static method call
		MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
		
		// TODO arraystorage
		
		
		if(agentclass)
		{
			// Check if there are dynamic beliefs
			// and enhance getter/setter beliefs by adding event call to setter
			List<String> tododyn = new ArrayList<String>();
			List<String> todoset = new ArrayList<String>();
			List<String> todoget = new ArrayList<String>();
			List<MBelief> mbels = model.getCapability().getBeliefs();
			for(MBelief mbel: mbels)
			{
				Collection<String> evs = mbel.getEvents();
				if(evs!=null && !evs.isEmpty() || mbel.isDynamic())
				{
					tododyn.add(mbel.getName());
				}
				
				if(!mbel.isFieldBelief())
				{
					todoset.add(mbel.getSetter().getName());
				}
				
				if(!mbel.isFieldBelief())
				{
					todoget.add(mbel.getGetter().getName());
				}
			}
			
			cn.fields.add(new FieldNode(Opcodes.ACC_PROTECTED, "__initargs", "Ljava/util/List;", new String[]{"Ljava/util/List<Ljadex/commons/Tuple3<Ljava/lang/Class<*>;[Ljava/lang/Class<*>;[Ljava/lang/Object;>;>;"}, null));
			
			for(MethodNode mn: mths)
			{
//				System.out.println(mn.name);
				
				// search constructor (should not have multiple ones) 
				// and extract field assignments for dynamic beliefs
				// will be incarnated as new update methods 
				if(mn.name.equals("<init>"))
				{
					InsnList l = mn.instructions;
					LabelNode begin = null;
					int foundcon = -1;
					
					for(int i=0; i<l.size(); i++)
					{
						AbstractInsnNode n = l.get(i);
						
						if(begin==null && n instanceof LabelNode)
						{
							begin = (LabelNode)n;
						}
						
						// find first constructor call
						if(Opcodes.INSN_INVOKE_DIRECT==n.getOpcode() && foundcon==-1)
						{
							foundcon = i;
							begin = null;
						}
						else if(n instanceof MethodInsnNode && ((MethodInsnNode)n).name.equals("writeField"))
						{
							MethodInsnNode min = (MethodInsnNode)n;
							
//							System.out.println("found writeField node: "+min.name+" "+min.getOpcode());
							AbstractInsnNode start = min;
							String name = null;
							List<String> evs = new ArrayList<String>(); 
							while(!start.equals(begin))
							{
								// find method name via last constant load
								if (name == null) {
									System.out.println(start);
									// TODO: find method name !!
								}
//								if(name==null && start instanceof LdcInsnNode)
//									name = (String)((LdcInsnNode)start).cst;
								if(isInstanceGetField(start.getOpcode()))
								{
									String bn = ((FieldInsnNode)start).name;
									if(model.getCapability().hasBelief(bn))
									{
										evs.add(bn);
									}
								}
								start = start.getPrevious();
							}
							
							// TODO: dynamic beliefs
//							if(tododyn.remove(name))
//							{
//								MBelief mbel = model.getCapability().getBelief(name);
//								mbel.getEvents().addAll(evs);
//								
//								MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC, IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX
//									+SUtil.firstToUpperCase(name), Type.getMethodDescriptor(Type.VOID_TYPE), null, null);
//								
//								// First labels are cloned
//								AbstractInsnNode cur = start;
//								Map<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
//								while(!cur.equals(min))
//								{
//									if(cur instanceof LabelNode)
//										labels.put((LabelNode)cur, new LabelNode(new Label()));
//									cur = cur.getNext();
//								}
//								// Then code is cloned
//								cur = start;
//								while(!cur.equals(min))
//								{
//									AbstractInsnNode clone = cur.clone(labels);
//									mnode.instructions.add(clone);
//									cur = cur.getNext();
//								}
//								mnode.instructions.add(cur.clone(labels));
//								mnode.visitInsn(Opcodes.RETURN);
//								
//								cn.methods.add(mnode);
//							}
							
							begin = null;
						}
					}
					
					// Move init code to separate method for being called after injections. 
					if(foundcon!=-1 && foundcon+1<l.size())
					{
						String name	= IBDIClassGenerator.INIT_EXPRESSIONS_METHOD_PREFIX+"_"+iclname.replace("/", "_").replace(".", "_");
//						System.out.println("Init method: "+name);
						MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC, name, mn.desc, mn.signature, null);
						cn.methods.add(mnode);

						while(l.size()>foundcon+1)
						{
							AbstractInsnNode	node	= l.get(foundcon+1);
							if(isReturn(node.getOpcode()))
							{
								mnode.visitInsn(node.getOpcode());
								break;
							}
							l.remove(node);
							mnode.instructions.add(node);
						}						
						
//						// Add code to store arguments in field.
//						Type[]	args	= Type.getArgumentTypes(mn.desc); // fails
						InsnList	init	= new InsnList();
//
						// obj param
//						init.add(new VarInsnNode(Opcodes.ALOAD, 0));
//						
//						// clazz param
//						init.add(new LdcInsnNode(Type.getType("L"+iclname+";")));
//						
//						// argtypes param
//						init.add(new LdcInsnNode(args.length));
//						init.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));
//						for(int i=0; i<args.length; i++)
//						{
//							init.add(new InsnNode(Opcodes.DUP));
//							init.add(new LdcInsnNode(i));
//							init.add(new LdcInsnNode(args[i]));
//							init.add(new InsnNode(Opcodes.AASTORE));
//						}
//						
//						// args param
//						init.add( new LdcInsnNode(args.length));
//						init.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
//						for(int i=0; i<args.length; i++)
//						{
//							init.add(new InsnNode(Opcodes.DUP));
//							init.add(new LdcInsnNode(i));
//							init.add(new VarInsnNode(Opcodes.ALOAD, i+1));	// 0==this, 1==arg0, ...
//							init.add(new InsnNode(Opcodes.AASTORE));
//						}
//						
//						// Invoke method.
						init.add(new MethodInsnNode(Opcodes.INSN_INVOKE_STATIC, "jadex/bdiv3/BDIAgent", "addInitArgs", "(Ljava/lang/Object;Ljava/lang/Class;[Ljava/lang/Class;[Ljava/lang/Object;)V", new int[]{1})); // TODO: register
//						
						l.insertBefore(l.get(foundcon+1), init);
					}
				} // constructor end
			}
		}
	}

	private InputStream getFileInputStream(File apkFile)
	{
		InputStream result = null;
		String desiredFile = "classes.dex";
		try
		{
			FileInputStream fin = new FileInputStream(apkFile);
			@SuppressWarnings("resource")
			// Resource is closed later
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null)
			{
				Logger.d("Unzipping " + ze.getName());

				if (!ze.isDirectory())
				{

					/**** Changes made below ****/
					if (ze.getName().toString().equals(desiredFile))
					{
						result = zin;
						break;
					}

				}

				zin.closeEntry();

			}
			// zin.close();
		}
		catch (Exception e)
		{
			Log.e("Decompress", "unzip", e);
		}

		return result;
	}

	protected boolean isInstancePutField(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_IPUT :
			case Opcodes.INSN_IPUT_BOOLEAN :
			case Opcodes.INSN_IPUT_BYTE :
			case Opcodes.INSN_IPUT_CHAR :
			case Opcodes.INSN_IPUT_OBJECT :
			case Opcodes.INSN_IPUT_SHORT :
			case Opcodes.INSN_IPUT_WIDE :
				return true;
			default :
				return false;
		}
	}
	
	protected boolean isInstanceGetField(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_IGET :
			case Opcodes.INSN_IGET_BOOLEAN :
			case Opcodes.INSN_IGET_BYTE :
			case Opcodes.INSN_IGET_CHAR :
			case Opcodes.INSN_IGET_OBJECT :
			case Opcodes.INSN_IGET_SHORT :
			case Opcodes.INSN_IGET_WIDE :
				return true;
			default :
				return false;
		}
	}
	
	protected boolean isReturn(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_RETURN :
			case Opcodes.INSN_RETURN_OBJECT :
			case Opcodes.INSN_RETURN_VOID :
			case Opcodes.INSN_RETURN_WIDE :
				return true;
			default :
				return false;
		}
	}

	
}
