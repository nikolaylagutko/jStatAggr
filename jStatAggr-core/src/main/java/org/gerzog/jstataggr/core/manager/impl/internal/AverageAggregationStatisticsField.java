/**
 * Copyright (C)2014 - Nikolay Lagutko <nikolay.lagutko@mail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gerzog.jstataggr.core.manager.impl.internal;

import javassist.CtClass;
import javassist.CtMethod;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.core.templates.TemplateHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AverageAggregationStatisticsField extends AggregationStatisticsField {

	private final AbstractStatisticsField countField;

	public AverageAggregationStatisticsField(final String fieldName, final Class<?> dataType, final AggregationType aggregationType) {
		super(fieldName, dataType, aggregationType);

		countField = new AggregationStatisticsField("protected", generateFieldName(), getDataType(), AggregationType.COUNT);
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		countField.generate(clazz);

		super.generate(clazz);
	}

	@Override
	protected CtMethod generateUpdater(final CtClass clazz) throws Exception {
		final CtMethod updater = super.generateUpdater(clazz);

		final String methodName = FieldUtils.getUpdaterName(countField.getFieldName(), AggregationType.COUNT);
		final String paramName = generateFieldName();

		updater.insertAfter(TemplateHelper.methodCall(methodName, paramName));

		return updater;
	}

	@Override
	protected String getUpdaterText() {
		return TemplateHelper.averageUpdater(getModifier(), getFieldName(), getDataType());
	}
}
