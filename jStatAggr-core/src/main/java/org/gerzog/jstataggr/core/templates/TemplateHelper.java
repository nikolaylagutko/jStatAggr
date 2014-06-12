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
package org.gerzog.jstataggr.core.templates;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.core.functions.FunctionHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class TemplateHelper {

	private static final String FUNCTION_HELPER_PREFIX = FunctionHelper.class
			.getName() + ".";

	private static final String APPLY_METHOD_PREFIX = FUNCTION_HELPER_PREFIX
			+ "apply";

	private static final String AGGREGATION_TYPE_PREFIX = AggregationType.class
			.getTypeName() + ".";

	private static final AggregationType[] SIMPLE_AGGREGATIONS = {
		AggregationType.MIN, AggregationType.MAX, AggregationType.SUM,
		AggregationType.COUNT };

	private TemplateHelper() {

	}

	public static String getter(final String modifier, final String name,
			final Class<?> returnType, final Class<?> fieldType) {
		return method(modifier, FieldUtils.getGetterName(name, returnType),
				getTypeName(returnType),
				getterBody(returnType, fieldAccessLine(name, fieldType)));
	}

	protected static String fieldAccessLine(final String field,
			final Class<?> type) {
		return field + getPostfix(type);
	}

	private static String getPostfix(final Class<?> type) {
		String postfix = StringUtils.EMPTY;

		if (TypeUtils.isAssignable(type, AtomicLong.class)
				|| TypeUtils.isAssignable(type, AtomicInteger.class)
				|| TypeUtils.isAssignable(type, LongAccumulator.class)) {
			postfix = ".get()";
		} else if (TypeUtils.isAssignable(type, LongAdder.class)) {
			postfix = ".sum()";
		}

		return postfix;
	}

	protected static String getterBody(final Class<?> returnType,
			final String name) {
		final StringBuilder builder = new StringBuilder();

		builder.append("return (").append(returnType.getName())
		.append(") this.").append(name).append(";");

		return builder.toString();
	}

	protected static String averageGetterBody(final String name) {
		final StringBuilder builder = new StringBuilder();

		builder.append("return this.")
		.append(FieldUtils.getAggregationFieldName(name,
				AggregationType.SUM))
				.append(" / this.")
				.append(FieldUtils.getAggregationFieldName(name,
						AggregationType.COUNT)).append(";");

		return builder.toString();
	}

	protected static String method(final String modifier, final String name,
			final String resultType, final String body,
			final Object... arguments) {
		final StringBuilder builder = new StringBuilder();

		builder.append(modifier).append(" ");

		if (resultType != null) {
			builder.append(resultType);
		} else {
			builder.append("void");
		}

		builder.append(" ").append(name);

		builder.append("(");

		for (int i = 0; i < (arguments.length / 2); i++) {
			final String argType = (String) arguments[2 * i];
			final String argName = (String) arguments[(2 * i) + 1];

			if (i > 0) {
				builder.append(", ");
			}

			builder.append(argType).append(" ").append(argName);
		}
		builder.append(") {").append(body).append("}");

		return builder.toString();
	}

	public static String setter(final String modifier, final String name,
			final Class<?> variableType) {
		return method(modifier, FieldUtils.getSetterName(name), null,
				setterBody(name), getTypeName(variableType), name);
	}

	private static boolean isSimpleAssignment(final Class<?> type) {
		if (type.isPrimitive() || type.equals(Integer.class)
				|| type.equals(Long.class)) {
			return true;
		}

		return false;
	}

	protected static String setterBody(final String name) {
		final StringBuilder builder = new StringBuilder();

		builder.append("this.").append(name).append(" = ").append(name)
		.append(";");

		return builder.toString();
	}

	public static String simpleUpdater(final String modifier,
			final String name, final Class<?> methodType,
			final Class<?> fieldType, final AggregationType aggregationType) {
		return method(modifier,
				FieldUtils.getUpdaterName(name, aggregationType), null,
				simpleUpdaterBody(name, aggregationType, fieldType),
				getTypeName(methodType), name);
	}

	public static String averageUpdater(final String modifier,
			final String name, final Class<?> methodType,
			final Class<?> fieldType) {
		final String averageFieldName = FieldUtils.getAggregationFieldName(
				name, AggregationType.AVERAGE);

		return method(modifier,
				FieldUtils.getUpdaterName(name, AggregationType.AVERAGE), null,
				averageUpdaterBody(averageFieldName, fieldType),
				getTypeName(methodType), averageFieldName);
	}

	protected static String averageUpdaterBody(final String name,
			final Class<?> fieldType) {
		final StringBuilder builder = new StringBuilder();
		final String countFieldName = FieldUtils.getAggregationFieldName(name,
				AggregationType.COUNT);

		final String methodCall = methodCall(APPLY_METHOD_PREFIX, name,
				countFieldName, "this." + name);

		if (isSimpleAssignment(fieldType)) {
			builder.append("this.").append(name).append(" = ");
		}

		builder.append(methodCall);

		return builder.toString();
	}

	protected static String simpleUpdaterBody(final String name,
			final AggregationType aggregationType, final Class<?> fieldType) {
		if (!ArrayUtils.contains(SIMPLE_AGGREGATIONS, aggregationType)) {
			throw new IllegalArgumentException(
					"<"
							+ aggregationType
							+ "> is not a simple aggregation and doesn't supported by this method");
		}

		final StringBuilder builder = new StringBuilder();

		final String fieldName = FieldUtils.getAggregationFieldName(name,
				aggregationType);

		final String methodCall = methodCall(APPLY_METHOD_PREFIX,
				AGGREGATION_TYPE_PREFIX + aggregationType.name(), name, "this."
						+ fieldName);

		if (isSimpleAssignment(fieldType)) {
			builder.append("this.").append(fieldName).append(" = ");
		}

		builder.append(methodCall);

		return builder.toString();
	}

	protected static String getTypeName(final Class<?> type) {
		if (type == null) {
			return null;
		}

		if (type.isArray()) {
			return getTypeName(type.getComponentType()) + "[]";
		}
		return type.getName();
	}

	public static String methodCall(final String methodName,
			final String... paramNames) {
		final StringBuilder builder = new StringBuilder();

		builder.append(methodName).append("(")
		.append(StringUtils.join(paramNames, ", ")).append(");");

		return builder.toString();
	}

}
