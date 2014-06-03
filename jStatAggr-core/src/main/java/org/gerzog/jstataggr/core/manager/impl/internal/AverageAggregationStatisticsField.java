package org.gerzog.jstataggr.core.manager.impl.internal;

import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AverageAggregationStatisticsField extends AggregationStatisticsField {

	private final List<IStatisticsField> dependent = new ArrayList<>();

	public AverageAggregationStatisticsField(final String fieldName, final Class<?> dataType, final AggregationType aggregationType) {
		super(fieldName, long.class, dataType, aggregationType);

		dependent.add(new AggregationStatisticsField(generateFieldName(), getDataType(), AggregationType.SUM));
		dependent.add(new AggregationStatisticsField(generateFieldName(), getDataType(), AggregationType.COUNT));
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		for (final IStatisticsField subField : dependent) {
			subField.generate(clazz);
		}

		super.generate(clazz);
	}
}
