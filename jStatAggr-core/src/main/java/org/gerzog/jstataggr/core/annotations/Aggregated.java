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
 * Mark field of a class as Aggregated.
 * 
 * This mean Statistics Manager will handle value of this field
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aggregated {
	
	/**
	 * Type of field Aggregation
	 * 
	 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
	 */
	public enum AggregationType {
		/*
		 * Will be aggregated only count of events (in case of array/collections it will be called corresponding method)
		 */
		COUNT,
		/*
		 * Will be aggregated maximal value of field
		 */
		MAX,
		/*
		 * Will be aggregated minimal value of field
		 */
		MIN,
		/*
		 * Will be aggregated average value of field
		 */
		AVERAGE,
		/*
		 * Will be aggregated sum of all values for this field
		 */
		SUM;
	}
	
	/**
	 * Aggregation types affected for annotated field
	 */
	public AggregationType[] value() default {};

}
