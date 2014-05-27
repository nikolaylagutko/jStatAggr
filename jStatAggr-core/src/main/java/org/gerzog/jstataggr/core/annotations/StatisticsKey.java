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
 * Marks a field to be a part of Statistics Key. 
 * 
 * This mean that values of non-key field will be grouped by values of key fields.
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StatisticsKey {

}
