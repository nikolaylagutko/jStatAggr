package org.gerzog.jstataggr.core.manager.impl.internal;

import org.gerzog.jstataggr.core.utils.FieldUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsField extends AbstractStatisticsField {

	public StatisticsField(final String originalFieldName, final Class<?> dataType) {
		super(originalFieldName, dataType);
	}

	@Override
	protected String getAccessMethodName() {
		return FieldUtils.getGetterName(generateFieldName(), getDataType());
	}

	@Override
	protected Class<?> getAccessMethodType() {
		return getDataType();
	}

}
