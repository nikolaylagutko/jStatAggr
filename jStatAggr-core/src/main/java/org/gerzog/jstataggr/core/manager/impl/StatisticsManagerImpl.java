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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gerzog.jstataggr.IStatisticsManager;
import org.gerzog.jstataggr.annotations.Aggregated;
import org.gerzog.jstataggr.annotations.StatisticsKey;
import org.gerzog.jstataggr.core.manager.impl.StatisticsCollector.StatisticsCollectorBuilder;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class StatisticsManagerImpl implements IStatisticsManager {

	private final Map<String, StatisticsCollector> collectors = new ConcurrentHashMap<>();

	@Override
	public void updateStatistics(final Object statisticsEntry, final Class<?> statisticsClass, final String statisticsName) {
		StatisticsCollector collector = collectors.get(statisticsName);

		if (collector == null) {
			collector = createCollector(statisticsClass, statisticsName);
		}

		collector.updateStatistics(statisticsEntry);
	}

	protected synchronized StatisticsCollector createCollector(final Class<?> statisticsClass, final String statisticsName) {
		final StatisticsCollectorBuilder builder = new StatisticsCollectorBuilder(statisticsName);

		initializeCollector(statisticsClass, builder);

		final StatisticsCollector result = builder.build();

		final StatisticsCollector previous = collectors.putIfAbsent(statisticsName, result);

		return previous == null ? result : result;
	}

	protected void initializeCollector(final Class<?> statisticsClass, final StatisticsCollectorBuilder builder) {
		for (final Field field : statisticsClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(StatisticsKey.class)) {
				appendStatisticsKeys(builder, statisticsClass, field);
			}

			if (field.isAnnotationPresent(Aggregated.class)) {
				final Aggregated annotation = field.getAnnotation(Aggregated.class);
				appendAggregation(builder, statisticsClass, field, annotation);
			}
		}
	}

	protected void appendStatisticsKeys(final StatisticsCollectorBuilder builder, final Class<?> statisticsClass, final Field field) {
		final MethodHandle getter = findGetter(statisticsClass, field);

		builder.addStatisticsKey(field, getter);
	}

	protected void appendAggregation(final StatisticsCollectorBuilder builder, final Class<?> statisticsClass, final Field field, final Aggregated annotation) {
		final MethodHandle getter = findGetter(statisticsClass, field);

		builder.addAggregation(field, annotation.value(), getter);
	}

	protected MethodHandle findGetter(final Class<?> statisticsClass, final Field field) {
		return propogate(() -> {
			final Method readMethod = new PropertyDescriptor(field.getName(), statisticsClass).getReadMethod();

			return MethodHandles.lookup().unreflect(readMethod);
		}, (e) -> new IllegalStateException("There is no public getter for field <" + field + ">", e));
	}

}
