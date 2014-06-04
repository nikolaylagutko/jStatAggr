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
package org.gerzog.jstataggr.core.collector.impl;

import static org.gerzog.jstataggr.core.utils.Throwables.propogate;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassPool;
import javassist.CtClass;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.IStatisticsFilter;
import org.gerzog.jstataggr.IStatisticsKey;
import org.gerzog.jstataggr.core.collector.impl.StatisticsKey.StatisticsKeyBuilder;
import org.gerzog.jstataggr.core.manager.impl.internal.IStatisticsField;
import org.gerzog.jstataggr.core.manager.impl.internal.StatisticsFields;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class StatisticsCollector {

	private static final String PACKAGE_PREFIX = "org.gerzog.jstataggr.core.manager.impl.generated.";

	public static class StatisticsCollectorBuilder {

		private final String className;

		private final StatisticsCollector result = new StatisticsCollector();

		private final Map<IStatisticsField, MethodHandle> fieldInfo = new HashMap<>();

		public StatisticsCollectorBuilder(final String className) {
			this.className = className;
		}

		public StatisticsCollectorBuilder addStatisticsKey(final Field field,
				final MethodHandle getter) {
			fieldInfo.put(
					StatisticsFields.forStatisticsKey(field.getName(),
							field.getType()), getter);

			return this;
		}

		public StatisticsCollectorBuilder addAggregation(final Field field,
				final AggregationType[] aggregationTypes,
				final MethodHandle getter) {
			for (final AggregationType type : aggregationTypes) {
				fieldInfo.put(
						StatisticsFields.forAggregation(field.getName(),
								field.getType(), type), getter);
			}

			return this;
		}

		public StatisticsCollector build() {
			result.classInfo = generateClassInfo(className, fieldInfo);

			return result;
		}

	}

	private static class CollectorClassInfo {
		private final Class<?> bucketClass;

		private final Map<String, Pair<MethodHandle, MethodHandle>> statisticsKeyHandles = new HashMap<>();

		private final List<Pair<MethodHandle, MethodHandle>> statisticsUpdaters = new ArrayList<>();

		public CollectorClassInfo(final Class<?> bucketClass) {
			this.bucketClass = bucketClass;
		}

		public Map<String, Pair<MethodHandle, MethodHandle>> getStatisticsKeyHandles() {
			return statisticsKeyHandles;
		}

		public List<Pair<MethodHandle, MethodHandle>> getStatisticsUpdaters() {
			return statisticsUpdaters;
		}

		public Class<?> getBucketClass() {
			return bucketClass;
		}
	}

	private CollectorClassInfo classInfo;

	private final Map<IStatisticsKey, Object> statistics = new ConcurrentHashMap<>();

	// LN: 2.06.2014, made package-visible for tests
	StatisticsCollector() {

	}

	protected static CollectorClassInfo generateClassInfo(
			final String className,
			final Map<IStatisticsField, MethodHandle> fieldInfo) {
		final ClassPool pool = ClassPool.getDefault();

		final CtClass clazz = pool.makeClass(PACKAGE_PREFIX
				+ StringUtils.capitalize(className));

		fieldInfo.keySet().forEach(info -> {
			propogate(() -> info.generate(clazz));
		});

		return propogate(() -> {
			return generateClassInfo(clazz.toClass(), fieldInfo);
		});
	}

	protected static CollectorClassInfo generateClassInfo(final Class<?> clazz,
			final Map<IStatisticsField, MethodHandle> fieldInfo) {
		final CollectorClassInfo result = new CollectorClassInfo(clazz);

		fieldInfo
				.forEach((info, getterMethod) -> {
					propogate(() -> {
						final MethodHandle accessMethod = info
								.getAccessMethodHandle(clazz);

						final Pair<MethodHandle, MethodHandle> methodPair = ImmutablePair
								.of(getterMethod, accessMethod);

						if (info.isAggregator()) {
							result.getStatisticsUpdaters().add(methodPair);
						} else {
							result.getStatisticsKeyHandles().put(
									info.getFieldName(), methodPair);
						}
					});
				});

		return result;
	}

	public void updateStatistics(final Object statisticsData) {
		final Object statisticsBucket = getStatisticsBucket(statisticsData);

		updateStatistics(statisticsBucket, statisticsData);
	}

	protected void updateStatistics(final Object statisticsBucket,
			final Object statisticsData) {
		classInfo.getStatisticsUpdaters().forEach(handles -> {
			propogate(() -> {
				final Object value = handles.getLeft().invoke(statisticsData);

				handles.getRight().invoke(statisticsBucket, value);
			});
		});
	}

	protected Object getStatisticsBucket(final Object statisticsData) {
		final IStatisticsKey key = generateStatisticsKey(statisticsData);

		Object statisticsBucket = statistics.get(key);

		if (statisticsBucket == null) {
			statisticsBucket = generateStatisticsBucket(key, statisticsData);
		}

		return statisticsBucket;
	}

	protected IStatisticsKey generateStatisticsKey(final Object statisticsData) {
		final StatisticsKeyBuilder builder = new StatisticsKeyBuilder();

		classInfo.getStatisticsKeyHandles().forEach((name, handles) -> {
			propogate(() -> {
				final Object value = handles.getLeft().invoke(statisticsData);

				builder.withParameter(name, value);
			});
		});

		return builder.build();
	}

	protected Object generateStatisticsBucket(final IStatisticsKey key,
			final Object statisticsData) {
		return propogate(() -> {
			final Object result = classInfo.getBucketClass().newInstance();

			classInfo.getStatisticsKeyHandles().forEach((name, handles) -> {
				propogate(() -> {
					final Object value = key.get(name);

					handles.getRight().invoke(result, value);
				});
			});

			final Object existing = statistics.putIfAbsent(key, result);

			return existing == null ? result : existing;
		});
	}

	protected Map<IStatisticsKey, Object> getStatistics() {
		return statistics;
	}

	public Collection<Object> collectStatistics(final IStatisticsFilter filter,
			final boolean cleanup) {
		final Collection<Object> result = new ArrayList<>();

		statistics.keySet().forEach(
				key -> {
					if (filter.isApplied(key)) {
						final Object data = cleanup ? statistics.remove(key)
								: statistics.get(key);

						result.add(data);
					}
				});

		return result;
	}
}
