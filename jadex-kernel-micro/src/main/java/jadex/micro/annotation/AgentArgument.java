package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marker for agent argument field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentArgument
{
	/**
	 *  The argument name.
	 *  Is optional. If not set the field name
	 *  must correspond to the argument name.
	 */
	public String value() default "";
}
