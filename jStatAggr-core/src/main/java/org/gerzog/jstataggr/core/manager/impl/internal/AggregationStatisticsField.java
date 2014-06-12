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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.FieldType;
import org.gerzog.jstataggr.core.templates.TemplateHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;
import org.gerzog.jstataggr.core.utils.InitializerUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AggregationStatisticsField extends AbstractStatisticsField {

	private final AggregationType aggregationType;

	protected AggregationStatisticsField(final String modifier, final String fieldName, final Class<?> methodClass, final Class<?> fieldClass, final AggregationType aggregationType, final FieldType fieldType) {
		super(modifier, fieldName, methodClass, defineFieldType(fieldClass, aggregationType, fieldType));

		this.aggregationType = aggregationType;
	}

	protected AggregationStatisticsField(final String fieldName, final Class<?> methodClass, final Class<?> fieldClass, final AggregationType aggregationType, final FieldType fieldType) {
		super(fieldName, methodClass, defineFieldType(fieldClass, aggregationType, fieldType));

		this.aggregationType = aggregationType;
	}

	public AggregationStatisticsField(final String fieldName, final Class<?> dataType, final AggregationType aggregationType, final FieldType fieldType) {
		this(fieldName, dataType, dataType, aggregationType, fieldType);
	}

	public AggregationStatisticsField(final String modifier, final String fieldName, final Class<?> dataType, final AggregationType aggregationType, final FieldType fieldType) {
		this(modifier, fieldName, dataType, dataType, aggregationType, fieldType);
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
		return TemplateHelper.simpleUpdater(getModifier(), getFieldName(), getMethodType(), getFieldType(), aggregationType);
	}

	@Override
	protected String generateFieldName() {
		return FieldUtils.getAggregationFieldName(super.generateFieldName(), aggregationType);
	}

	@Override
	protected void addField(final CtClass clazz, final CtField field) throws Exception {
		clazz.addField(field, InitializerUtils.getInitializer(getFieldType(), aggregationType));
	}

	@Override
	protected String getAccessMethodName() {
		return FieldUtils.getUpdaterName(getFieldName(), aggregationType);
	}

	@Override
	protected Class<?> getAccessMethodType() {
		return getMethodType();
	}

	@Override
	public boolean isAggregator() {
		return true;
	}

	protected static Class<?> defineFieldType(final Class<?> originalFieldType, final AggregationType aggregation, final FieldType fieldType) {
		switch (fieldType) {
		case PRIMITIVE:
			return originalFieldType;
		case ATOMIC:
			return getAtomicType(originalFieldType);
		default:
			throw new IllegalStateException("Unsupported FieldType <" + fieldType + ">");
		}
	}

	private static Class<?> getAtomicType(final Class<?> type) {
		if (type.equals(Long.class) || type.equals(long.class)) {
			return AtomicLong.class;
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			return AtomicInteger.class;
		}

		throw new IllegalArgumentException("Unsupported type <" + type.getSimpleName() + ">");
	}
}
