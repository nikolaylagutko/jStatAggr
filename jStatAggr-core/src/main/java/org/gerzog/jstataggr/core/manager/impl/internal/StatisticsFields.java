package org.gerzog.jstataggr.core.manager.impl.internal;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class StatisticsFields {

	private StatisticsFields() {

	}

	public static IStatisticsField forStatisticsKey(final String name, final Class<?> dataType) {
		return new StatisticsField(name, dataType);
	}

	public static IStatisticsField forAggregation(final String name, final Class<?> dataType, final AggregationType aggregationType) {
		switch (aggregationType) {
		case MIN:
		case MAX:
		case SUM:
			return new AggregationStatisticsField(name, dataType, aggregationType);
		case COUNT:
			return new CountAggregationStatisticsField(name, dataType, aggregationType);
		case AVERAGE:
			return new AverageAggregationStatisticsField(name, dataType, aggregationType);
		default:
			throw new IllegalStateException("Unsupported enum <" + aggregationType + ">");
		}
	}

}
