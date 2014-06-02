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
package org.gerzog.jstataggr.core.utils;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class FieldUtils {

	private static final String DEFAULT_GETTER_PREFIX = "get";

	private static final String DEFAULT_SETTER_PREFIX = "set";

	private static final String BOOLEAN_GETTER_PREFIX = "is";

	private static final String UPDATER_PREFIX = "update";

	private FieldUtils() {

	}

	public static String getGetterName(final String name, final Class<?> type) {
		final String prefix = type.equals(Boolean.class) || type.equals(boolean.class) ? BOOLEAN_GETTER_PREFIX : DEFAULT_GETTER_PREFIX;

		return prefix + StringUtils.capitalize(name);
	}

	public static String getSetterName(final Field field) {
		return getSetterName(field.getName());
	}

	public static String getSetterName(final String name) {
		final String prefix = DEFAULT_SETTER_PREFIX;

		return prefix + StringUtils.capitalize(name);
	}

	public static String getUpdaterName(final String name, final AggregationType aggregationType) {
		return UPDATER_PREFIX + StringUtils.capitalize(getAggregationFieldName(name, aggregationType));
	}

	public static String getAggregationFieldName(final String name, final AggregationType aggregationType) {
		return name + getAggregationPostfix(aggregationType);
	}

	private static String getAggregationPostfix(final AggregationType aggregationType) {
		return StringUtils.capitalize(StringUtils.lowerCase(aggregationType.name()));
	}

}
