package org.gerzog.jstataggr.core.manager.impl.internal;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class CountAggregationStatisticsField extends AggregationStatisticsField {

	protected CountAggregationStatisticsField(final String fieldName, final Class<?> dataType, final AggregationType aggregationType) {
		super(fieldName, long.class, dataType, aggregationType);
	}
}
