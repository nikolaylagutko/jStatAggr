/**
 *
 */
package org.gerzog.jstataggr.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class marked with this annotation will be handled as a single portion of
 * Statistics Data
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StatisticsEntry {

	/**
	 * Name of Statistics (default is name of class)
	 */
	public String value() default "";

}
