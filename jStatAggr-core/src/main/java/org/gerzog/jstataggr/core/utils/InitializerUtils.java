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

import javassist.CtField.Initializer;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class InitializerUtils {

	private static final int ZERO = 0;

	private InitializerUtils() {

	}

	public static Initializer getInitializer(final Class<?> type, final AggregationType aggregation) {
		switch (aggregation) {
		case MIN:
			return getMinInitializer(type);
		case MAX:
			return getMaxInitializer(type);
		case SUM:
			return getSumInitializer(type);
		default:
			throw new IllegalArgumentException("Initializer for <" + aggregation + "> is not yet defined");
		}
	}

	private static Initializer getMaxInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(Integer.MIN_VALUE);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(Long.MIN_VALUE);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

	private static Initializer getMinInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(Integer.MAX_VALUE);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(Long.MAX_VALUE);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

	private static Initializer getSumInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(ZERO);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(ZERO);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

}
