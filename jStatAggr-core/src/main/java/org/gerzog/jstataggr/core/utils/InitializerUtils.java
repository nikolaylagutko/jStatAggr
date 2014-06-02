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

	private static final int ZERO_INT = 0;

	private static final long ZERO_LONG = 0l;

	private InitializerUtils() {

	}

	public static Initializer getInitializer(final Class<?> type, final AggregationType aggregation) {
		switch (aggregation) {
		case MIN:
			return getMaxValueInitializer(type);
		case MAX:
			return getMinValueInitializer(type);
		case SUM:
		case COUNT:
		case AVERAGE:
			return getZeroInitializer(type);
		default:
			throw new IllegalArgumentException("Initializer for <" + aggregation + "> is not yet defined");
		}
	}

	private static Initializer getMinValueInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(Integer.MIN_VALUE);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(Long.MIN_VALUE);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

	private static Initializer getMaxValueInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(Integer.MAX_VALUE);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(Long.MAX_VALUE);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

	private static Initializer getZeroInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(ZERO_INT);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(ZERO_LONG);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

}
