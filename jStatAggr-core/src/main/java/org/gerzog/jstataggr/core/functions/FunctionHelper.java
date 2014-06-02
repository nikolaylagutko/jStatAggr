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
package org.gerzog.jstataggr.core.functions;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class FunctionHelper {

	private FunctionHelper() {

	}

	public static int apply(final AggregationType type, final int update, final int current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case COUNT:
			throw new IllegalStateException("Method apply() for int didn's support COUNT type.");
		case MAX:
			return Math.max(update, current);
		case MIN:
			return Math.min(update, current);
		case SUM:
			return update + current;
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static long apply(final AggregationType type, final long update, final long current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case COUNT:
			throw new IllegalStateException("Method apply() for long didn's support COUNT type.");
		case MAX:
			return Math.max(update, current);
		case MIN:
			return Math.min(update, current);
		case SUM:
			return update + current;
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}
}
