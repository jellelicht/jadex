package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The result annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Result
{
	/**
	 *  The argument name.
	 */
	public String name();
	
	/**
	 *  The description.
	 */
	public String description() default "";
	
	/**
	 *  The type name.
	 */
	public String typename();
}
