package jadex.commons;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import io.github.lukehutch.fastclasspathscanner.scanner.AnnotationInfo;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanSpec;
import io.github.lukehutch.fastclasspathscanner.utils.LogNode;
import jadex.bytecode.vmhacks.VmHacks;

/**
 *  Class using the internal fast class path scanner to provide
 *  some utility methods for inspecting raw binary classes.
 *
 */
public class SFastClassUtils
{
	protected volatile static boolean INITIALIZED = false;
	
	/** Constructor for ClassfileBinaryParser */
	protected static MethodHandle CLASSFILEBINARYPARSER_CON;
	
	/** Method readClassInfoFromClassfileHeader. */
	protected static MethodHandle READCLASSINFOFROMCLASSFILEHEADER;
	
	protected static MethodHandle CLASSANNOTATIONS_FIELD;
	
	@SuppressWarnings("unchecked")
	public static List<AnnotationInfo> getAnnotationInfos(String filepath, ClassLoader cl)
	{
		initialize();
		
		List<AnnotationInfo> ret = null;
		InputStream is = null;
		try
		{
			is = cl.getResourceAsStream(filepath);
			
			ScanSpec spec = new ScanSpec(new String[0], null);
			
			Object cbp = CLASSFILEBINARYPARSER_CON.invokeExact();
			Object ciu = READCLASSINFOFROMCLASSFILEHEADER.invoke(cbp, null, filepath, is, spec, null);
			
			ret = (List<AnnotationInfo>) CLASSANNOTATIONS_FIELD.invoke(ciu);
		}
		catch (Throwable e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(is);
		}
		return ret;
	}
	
	/**
	 *  Lazy static initialization for the class.
	 */
	protected static void initialize()
	{
		if (INITIALIZED)
		{
			return;
		}
		
		synchronized (SFastClassUtils.class)
		{
			if (INITIALIZED)
			{
				return;
			}
			
			try
			{
				Lookup lookup = MethodHandles.lookup();
				Class<?> cbpclazz = Class.forName("io.github.lukehutch.fastclasspathscanner.scanner.ClassfileBinaryParser");
				Constructor<?> cbpcon = cbpclazz.getDeclaredConstructor();
				VmHacks.get().setAccessible(cbpcon, true);
				CLASSFILEBINARYPARSER_CON = lookup.unreflectConstructor(cbpcon).asType(MethodType.genericMethodType(0));
				
				Class<?> ceclazz = Class.forName("io.github.lukehutch.fastclasspathscanner.scanner.ClasspathElement");
				
				Class<?> ciuclazz = Class.forName("io.github.lukehutch.fastclasspathscanner.scanner.ClassInfoUnlinked");
				
				Method readclassinfofromclassfileheader = cbpclazz.getDeclaredMethod("readClassInfoFromClassfileHeader", ceclazz, String.class, InputStream.class, ScanSpec.class, LogNode.class);
				VmHacks.get().setAccessible(readclassinfofromclassfileheader, true);
				READCLASSINFOFROMCLASSFILEHEADER = lookup.unreflect(readclassinfofromclassfileheader);
				
				Field classannotations = ciuclazz.getDeclaredField("classAnnotations");
				VmHacks.get().setAccessible(classannotations, true);
				CLASSANNOTATIONS_FIELD = lookup.unreflectGetter(classannotations);
				
				INITIALIZED = true;
			}
			catch (Exception e)
			{
			}
		}
	}
}
