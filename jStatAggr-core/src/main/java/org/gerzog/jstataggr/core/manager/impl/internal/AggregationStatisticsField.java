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
import javassist.CtField;
import javassist.CtMethod;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.core.templates.TemplateHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;
import org.gerzog.jstataggr.core.utils.InitializerUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AggregationStatisticsField extends AbstractStatisticsField {

	private final Class<?> updaterType;

	private final AggregationType aggregationType;

	protected AggregationStatisticsField(final String modifier, final String fieldName, final Class<?> dataType, final Class<?> updaterType, final AggregationType aggregationType) {
		super(modifier, fieldName, dataType);

		this.updaterType = updaterType;
		this.aggregationType = aggregationType;
	}

	protected AggregationStatisticsField(final String fieldName, final Class<?> dataType, final Class<?> updaterType, final AggregationType aggregationType) {
		super(fieldName, dataType);

		this.updaterType = updaterType;
		this.aggregationType = aggregationType;
	}

	public AggregationStatisticsField(final String fieldName, final Class<?> dataType, final AggregationType aggregationType) {
		this(fieldName, dataType, dataType, aggregationType);
	}

	public AggregationStatisticsField(final String modifier, final String fieldName, final Class<?> dataType, final AggregationType aggregationType) {
		this(modifier, fieldName, dataType, dataType, aggregationType);
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		super.generate(clazz);

		generateUpdater(clazz);
	}

	protected CtMethod generateUpdater(final CtClass clazz) throws Exception {
		final CtMethod method = CtMethod.make(getUpdaterText(), clazz);

		clazz.addMethod(method);

		return method;
	}

	protected String getUpdaterText() {
		return TemplateHelper.simpleUpdater(getModifier(), getFieldName(), updaterType, aggregationType);
	}

	@Override
	protected String generateFieldName() {
		return FieldUtils.getAggregationFieldName(super.generateFieldName(), aggregationType);
	}

	@Override
	protected void addField(final CtClass clazz, final CtField field) throws Exception {
		clazz.addField(field, InitializerUtils.getInitializer(getDataType(), aggregationType));
	}

	@Override
	protected String getAccessMethodName() {
		return FieldUtils.getUpdaterName(getFieldName(), aggregationType);
	}

	@Override
	protected Class<?> getAccessMethodType() {
		return updaterType;
	}

	@Override
	public boolean isAggregator() {
		return true;
	}
}
