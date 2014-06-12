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
package org.gerzog.jstataggr.core.manager.impl;

import static org.gerzog.jstataggr.core.utils.Throwables.propogate;

import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.IStatisticsFilter;
import org.gerzog.jstataggr.IStatisticsManager;
import org.gerzog.jstataggr.annotations.Aggregated;
import org.gerzog.jstataggr.annotations.Expression;
import org.gerzog.jstataggr.annotations.StatisticsKey;
import org.gerzog.jstataggr.core.collector.impl.StatisticsCollector;
import org.gerzog.jstataggr.core.collector.impl.StatisticsCollector.StatisticsCollectorBuilder;
import org.gerzog.jstataggr.core.expressions.IExpressionHandler;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class StatisticsManagerImpl implements IStatisticsManager {

	private static final Class<?>[] SUPPORTED_TYPES_FOR_MIN_AGGREGATION = { Long.class, long.class, Integer.class, int.class };

	private static final Class<?>[] SUPPORTED_TYPES_FOR_MAX_AGGREGATION = { Long.class, long.class, Integer.class, int.class };

	private static final Class<?>[] SUPPORTED_TYPES_FOR_SUM_AGGREGATION = { Long.class, long.class, Integer.class, int.class };

	private static final Class<?>[] SUPPORTED_TYPES_FOR_COUNT_AGGREGATION = null;

	private static final Class<?>[] SUPPORTED_TYPES_FOR_AVERAGE_AGGREGATION = { Long.class, long.class, Integer.class, int.class };

	private final Map<String, StatisticsCollector> collectors = new ConcurrentHashMap<>();

	private IExpressionHandler expressionHandler;

	public StatisticsManagerImpl() {
		this(null);
	}

	public StatisticsManagerImpl(final IExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	public void setExpressionHandler(final IExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	@Override
	public void updateStatistics(final Object statisticsEntry, final String statisticsName) {
		StatisticsCollector collector = collectors.get(statisticsName);

		if (collector == null) {
			collector = createCollector(statisticsEntry.getClass(), statisticsName);
		}

		collector.updateStatistics(statisticsEntry);
	}

	protected synchronized StatisticsCollector createCollector(final Class<?> statisticsClass, final String statisticsName) {
		final StatisticsCollectorBuilder builder = new StatisticsCollectorBuilder(statisticsName, expressionHandler);

		initializeCollector(statisticsClass, builder);

		final StatisticsCollector result = builder.build();

		final StatisticsCollector previous = collectors.putIfAbsent(statisticsName, result);

		return previous == null ? result : result;
	}

	protected void initializeCollector(final Class<?> statisticsClass, final StatisticsCollectorBuilder builder) {
		for (final Field field : statisticsClass.getDeclaredFields()) {
			String expression = null;

			if (field.isAnnotationPresent(Expression.class)) {
				final Expression annotation = field.getAnnotation(Expression.class);

				validateExpessionField(field, annotation);

				expression = annotation.value();
			}

			if (field.isAnnotationPresent(StatisticsKey.class)) {
				validateStatisticsKeyField(field);

				appendStatisticsKeys(builder, statisticsClass, field, expression);
			}

			if (field.isAnnotationPresent(Aggregated.class)) {
				final Aggregated annotation = field.getAnnotation(Aggregated.class);

				validateAggregationField(field, annotation);

				appendAggregation(builder, statisticsClass, field, annotation, expression);
			}
		}
	}

	protected void appendStatisticsKeys(final StatisticsCollectorBuilder builder, final Class<?> statisticsClass, final Field field, final String expression) {
		final MethodHandle getter = findGetter(statisticsClass, field);

		builder.addStatisticsKey(field, getter, expression);
	}

	protected void appendAggregation(final StatisticsCollectorBuilder builder, final Class<?> statisticsClass, final Field field, final Aggregated annotation, final String expression) {
		final MethodHandle getter = findGetter(statisticsClass, field);

		builder.addAggregation(field, annotation.value(), annotation.fieldType(), getter, expression);
	}

	protected MethodHandle findGetter(final Class<?> statisticsClass, final Field field) {
		return propogate(() -> {
			final Method readMethod = new PropertyDescriptor(field.getName(), statisticsClass).getReadMethod();

			return MethodHandles.lookup().unreflect(readMethod);
		}, (e) -> new IllegalStateException("There is no public getter for field <" + field + ">", e));
	}

	@Override
	public Map<String, Collection<Object>> collectStatistics(final String statisticsName, final IStatisticsFilter filter, final boolean cleanup) {
		final Map<String, Collection<Object>> result = new HashMap<>();

		collectors.forEach((name, collector) -> {
			if ((statisticsName == null) || name.equals(statisticsName)) {
				result.put(name, collector.collectStatistics(filter, cleanup));
			}
		});

		return result;
	}

	protected Map<String, StatisticsCollector> getCollectors() {
		return collectors;
	}

	protected void validateStatisticsKeyField(final Field field) {
		if (field.isAnnotationPresent(Aggregated.class)) {
			throw new IllegalStateException("StatisticsKey field <" + field.getName() + "> cannot be marked as @Aggregated");
		}
	}

	protected void validateAggregationField(final Field field, final Aggregated annotation) {
		for (final AggregationType type : annotation.value()) {
			Class<?>[] supportedTypes = null;

			switch (type) {
			case AVERAGE:
				supportedTypes = SUPPORTED_TYPES_FOR_AVERAGE_AGGREGATION;
				break;
			case COUNT:
				supportedTypes = SUPPORTED_TYPES_FOR_COUNT_AGGREGATION;
				break;
			case MAX:
				supportedTypes = SUPPORTED_TYPES_FOR_MAX_AGGREGATION;
				break;
			case MIN:
				supportedTypes = SUPPORTED_TYPES_FOR_MIN_AGGREGATION;
				break;
			case SUM:
				supportedTypes = SUPPORTED_TYPES_FOR_SUM_AGGREGATION;
				break;
			default:
				throw new IllegalArgumentException("AggregationType <" + type + "> is not supported");
			}

			boolean valid = false;

			if (supportedTypes != null) {
				for (final Class<?> supported : supportedTypes) {
					if (field.getType().isAssignableFrom(supported)) {
						valid = true;
						break;
					}
				}
			} else {
				valid = true;
			}

			if (!valid) {
				throw new IllegalStateException("Field <" + field.getName() + "> is of unsupported type <" + field.getType().getSimpleName() + "> for aggregation <" + type + ">");
			}
		}
	}

	protected void validateExpessionField(final Field field, final Expression annotation) {
		if (expressionHandler == null) {
			throw new IllegalStateException("Field <" + field.getName() + "> cannot be annotated with @Expression since ExpressionHandler was not initialized");
		}

		final String expression = annotation.value();

		if (StringUtils.isEmpty(expression)) {
			throw new IllegalArgumentException("Expression cannot be an empty string");
		}
	}
}
