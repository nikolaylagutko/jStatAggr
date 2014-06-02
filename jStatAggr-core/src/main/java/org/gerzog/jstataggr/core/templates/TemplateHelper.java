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

import org.apache.commons.lang3.ArrayUtils;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.core.functions.FunctionHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class TemplateHelper {

	private static final String FUNCTION_HELPER_PREFIX = FunctionHelper.class.getName() + ".";

	private static final String APPLY_METHOD_PREFIX = FUNCTION_HELPER_PREFIX + "apply(";

	private static final String AGGREGATION_TYPE_PREFIX = AggregationType.class.getTypeName() + ".";

	private static final AggregationType[] SIMPLE_AGGREGATIONS = { AggregationType.MIN, AggregationType.MAX, AggregationType.SUM };

	private TemplateHelper() {

	}

	public static String getter(final String name, final Class<?> type) {
		return method(FieldUtils.getGetterName(name, type), type, getterBody(name));
	}

	protected static String getterBody(final String name) {
		final StringBuilder builder = new StringBuilder();

		builder.append("return this.").append(name).append(";");

		return builder.toString();
	}

	protected static String method(final String name, final Class<?> result, final String body, final Object... arguments) {
		final StringBuilder builder = new StringBuilder();

		builder.append("public ");

		if (result != null) {
			builder.append(result.getName());
		} else {
			builder.append("void");
		}

		builder.append(" ").append(name);

		builder.append("(");

		for (int i = 0; i < (arguments.length / 2); i++) {
			final Class<?> argType = (Class<?>) arguments[2 * i];
			final String argName = (String) arguments[(2 * i) + 1];

			if (i > 0) {
				builder.append(", ");
			}

			builder.append(argType.getName()).append(" ").append(argName);
		}
		builder.append(") {").append(body).append("}");

		return builder.toString();
	}

	public static String setter(final String name, final Class<?> type) {
		return method(FieldUtils.getSetterName(name), null, setterBody(name), type, name);
	}

	protected static String setterBody(final String name) {
		final StringBuilder builder = new StringBuilder();

		builder.append("this.").append(name).append(" = ").append(name).append(";");

		return builder.toString();
	}

	public static String simpleUpdater(final String name, final Class<?> type, final AggregationType aggregationType) {
		return method(FieldUtils.getUpdaterName(name, aggregationType), null, simpleUpdaterBody(name, aggregationType), type, name);
	}

	protected static String simpleUpdaterBody(final String name, final AggregationType aggregationType) {
		if (!ArrayUtils.contains(SIMPLE_AGGREGATIONS, aggregationType)) {
			throw new IllegalArgumentException("<" + aggregationType + "> is not a simple aggregation and doesn't supported by this method");
		}

		final StringBuilder builder = new StringBuilder();

		builder.append(APPLY_METHOD_PREFIX).append(AGGREGATION_TYPE_PREFIX).append(aggregationType.name()).append(", this.").append(name).append(", ").append(name).append(");");

		return builder.toString();
	}
}
