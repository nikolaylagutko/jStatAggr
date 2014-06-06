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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class FunctionHelper {

	private FunctionHelper() {

	}

	public static void apply(final AggregationType type, final int update, final LongAccumulator current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case COUNT:
		case SUM:
			throw new IllegalStateException("Method apply() for LongAccumulator didn't support <" + type + "> aggregation");
		case MAX:
		case MIN:
			current.accumulate(update);
			break;
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static void apply(final AggregationType type, final int update, final LongAdder current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case MAX:
		case MIN:
			throw new IllegalStateException("Method apply() for LongAdder didn't support <" + type + "> aggregation");
		case COUNT:
			current.increment();
			break;
		case SUM:
			current.add(update);
			break;
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static void apply(final AggregationType type, final int update, final AtomicInteger current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case COUNT:
			current.incrementAndGet();
			break;
		case MAX:
			final int currentMaxInt = current.get();
			current.compareAndSet(currentMaxInt, Math.max(update, currentMaxInt));
			break;
		case MIN:
			final int currentMinInt = current.get();
			current.compareAndSet(currentMinInt, Math.min(update, currentMinInt));
			break;
		case SUM:
			current.addAndGet(update);
			break;
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static void apply(final AggregationType type, final long update, final AtomicLong current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case COUNT:
			current.incrementAndGet();
			break;
		case MAX:
			final long currentMaxInt = current.get();
			current.compareAndSet(currentMaxInt, Math.max(update, currentMaxInt));
			break;
		case MIN:
			final long currentMinInt = current.get();
			current.compareAndSet(currentMinInt, Math.min(update, currentMinInt));
			break;
		case SUM:
			current.addAndGet(update);
			break;
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static int apply(final AggregationType type, final int update, final int current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() didn's support AVERAGE type. Please use applyAverage instread");
		case COUNT:
			return current + 1;
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
			return current + 1;
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

	public static long apply(final AggregationType type, final int update, final long current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() for Object didn's support COUNT type.");
		case COUNT:
			return current + getCount(update);
		case MAX:
			throw new IllegalStateException("Method apply() for long didn's support MAX type.");
		case MIN:
			throw new IllegalStateException("Method apply() for long didn's support MIN type.");
		case SUM:
			throw new IllegalStateException("Method apply() for long didn's support SUM type.");
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static long apply(final AggregationType type, final Object update, final long current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() for Object didn's support COUNT type.");
		case COUNT:
			return current + getCount(update);
		case MAX:
			throw new IllegalStateException("Method apply() for long didn's support MAX type.");
		case MIN:
			throw new IllegalStateException("Method apply() for long didn's support MIN type.");
		case SUM:
			throw new IllegalStateException("Method apply() for long didn's support SUM type.");
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static void apply(final AggregationType type, final Object update, final AtomicLong current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() for Object didn's support COUNT type.");
		case COUNT:
			current.addAndGet(getCount(update));
			break;
		case MAX:
			throw new IllegalStateException("Method apply() for long didn's support MAX type.");
		case MIN:
			throw new IllegalStateException("Method apply() for long didn's support MIN type.");
		case SUM:
			throw new IllegalStateException("Method apply() for long didn's support SUM type.");
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	public static void apply(final AggregationType type, final Object update, final LongAdder current) {
		switch (type) {
		case AVERAGE:
			throw new IllegalStateException("Method apply() for Object didn's support COUNT type.");
		case COUNT:
			current.add(getCount(update));
			break;
		case MAX:
			throw new IllegalStateException("Method apply() for long didn's support MAX type.");
		case MIN:
			throw new IllegalStateException("Method apply() for long didn's support MIN type.");
		case SUM:
			throw new IllegalStateException("Method apply() for long didn's support SUM type.");
		default:
			throw new IllegalArgumentException("Unsupported aggregation type <" + type + ">");
		}
	}

	private static int getCount(final Object o) {
		if (o instanceof Collection<?>) {
			return ((Collection<?>) o).size();
		} else if (o.getClass().isArray()) {
			return Array.getLength(o);
		} else if (o instanceof Map<?, ?>) {
			return ((Map<?, ?>) o).size();
		}
		return 1;
	}

	public static long apply(final long update, final long currentCount, final long currentAverage) {
		final long sum = currentCount * currentAverage;

		return (sum + update) / (currentCount + 1);
	}

	public static int apply(final int update, final int currentCount, final int currentAverage) {
		final int sum = currentCount * currentAverage;

		return (sum + update) / (currentCount + 1);
	}

	public static void apply(final long update, final AtomicLong currentCount, final AtomicLong currentAverage) {
		final long currentCountLong = currentCount.get();
		final long sum = currentCountLong * currentAverage.get();

		currentAverage.set((sum + update) / (currentCountLong + 1));
	}

	public static void apply(final int update, final AtomicInteger currentCount, final AtomicInteger currentAverage) {
		final int currentCountLong = currentCount.get();
		final int sum = currentCountLong * currentAverage.get();

		currentAverage.set((sum + update) / (currentCountLong + 1));
	}
}
