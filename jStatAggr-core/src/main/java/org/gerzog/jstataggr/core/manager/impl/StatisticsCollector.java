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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.core.manager.impl.StatisticsKey.StatisticsKeyBuilder;
import org.gerzog.jstataggr.core.templates.TemplateHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;
import org.gerzog.jstataggr.core.utils.InitializerUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class StatisticsCollector {

	private static final String PACKAGE_PREFIX = "org.gerzog.jstataggr.core.manager.impl.generated.";

	public static class StatisticsCollectorBuilder {

		private final String className;

		private final StatisticsCollector result = new StatisticsCollector();

		private final Map<Field, MethodHandle> statisticsKeys = new HashMap<>();

		private final Map<Field, MethodHandle> aggregations = new HashMap<>();

		private final Map<Field, List<AggregationType>> aggregationTypes = new HashMap<>();

		public StatisticsCollectorBuilder(final String className) {
			this.className = className;
		}

		public StatisticsCollectorBuilder addStatisticsKey(final Field field, final MethodHandle getter) {
			statisticsKeys.put(field, getter);

			return this;
		}

		public StatisticsCollectorBuilder addAggregation(final Field field, final AggregationType[] aggregationTypes, final MethodHandle getter) {
			aggregations.put(field, getter);
			this.aggregationTypes.put(field, Arrays.asList(aggregationTypes));

			return this;
		}

		public StatisticsCollector build() {
			result.classInfo = generateClassInfo(className, statisticsKeys, aggregations, aggregationTypes);

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

	private final Map<StatisticsKey, Object> statistics = new ConcurrentHashMap<>();

	// LN: 2.06.2014, made package-visible for tests
	StatisticsCollector() {

	}

	protected static CollectorClassInfo generateClassInfo(final String className, final Map<Field, MethodHandle> statisticsKeys, final Map<Field, MethodHandle> aggregations, final Map<Field, List<AggregationType>> aggregationTypes) {
		final ClassPool pool = ClassPool.getDefault();

		final CtClass clazz = pool.makeClass(PACKAGE_PREFIX + StringUtils.capitalize(className));

		addProperties(clazz, statisticsKeys.keySet(), pool, null);
		addProperties(clazz, aggregations.keySet(), pool, aggregationTypes);
		addUpdaters(clazz, aggregations.keySet(), aggregationTypes, pool);

		return propogate(() -> generateClassInfo(clazz.toClass(), statisticsKeys, aggregations, aggregationTypes));
	}

	protected static CollectorClassInfo generateClassInfo(final Class<?> clazz, final Map<Field, MethodHandle> statisticsKeys, final Map<Field, MethodHandle> aggregations, final Map<Field, List<AggregationType>> aggregationTypes) {
		final CollectorClassInfo result = new CollectorClassInfo(clazz);

		statisticsKeys.forEach((field, handle) -> {
			propogate(() -> {
				final String fieldName = field.getName();

				final Method setter = clazz.getMethod(FieldUtils.getSetterName(field), field.getType());
				final MethodHandle setterHandle = MethodHandles.lookup().unreflect(setter);

				result.getStatisticsKeyHandles().put(fieldName, ImmutablePair.of(handle, setterHandle));
			});
		});

		aggregations.forEach((field, handle) -> {
			aggregationTypes.get(field).forEach(aggregationType -> {
				propogate(() -> {
					final Method updater = clazz.getMethod(FieldUtils.getUpdaterName(field.getName(), aggregationType), field.getType());
					final MethodHandle updaterHandle = MethodHandles.lookup().unreflect(updater);

					result.getStatisticsUpdaters().add(ImmutablePair.of(handle, updaterHandle));
				});
			});
		});

		return result;
	}

	protected static void addProperties(final CtClass clazz, final Set<Field> fields, final ClassPool pool, final Map<Field, List<AggregationType>> aggregationTypes) {
		fields.forEach(field -> {
			if (aggregationTypes == null) {
				addProperty(clazz, field, pool, null);
			} else {
				aggregationTypes.get(field).forEach(aggregationType -> addProperty(clazz, field, pool, aggregationType));
			}
		});
	}

	protected static void addUpdaters(final CtClass clazz, final Set<Field> fields, final Map<Field, List<AggregationType>> aggregations, final ClassPool pool) {
		fields.forEach(field -> {
			aggregations.get(field).forEach(aggregation -> {
				switch (aggregation) {
				case MIN:
				case MAX:
				case SUM:
					addSimpleUpdater(clazz, field, aggregation, pool);
					break;
				default:
					throw new UnsupportedOperationException("Updater for <" + aggregation + "> aggregation type is not implemented yet");
				}
			});
		});
	}

	protected static void addSimpleUpdater(final CtClass clazz, final Field field, final AggregationType aggregation, final ClassPool pool) {
		propogate(() -> {
			final CtMethod updater = CtMethod.make(TemplateHelper.simpleUpdater(field.getName(), field.getType(), aggregation), clazz);

			clazz.addMethod(updater);
		});
	}

	protected static void addProperty(final CtClass clazz, final Field field, final ClassPool pool, final AggregationType aggregationType) {
		propogate(() -> {
			final String fieldName = aggregationType == null ? field.getName() : FieldUtils.getAggregationFieldName(field.getName(), aggregationType);

			final CtClass type = pool.getCtClass(field.getType().getName());

			final CtField ctField = new CtField(type, fieldName, clazz);
			if (aggregationType != null) {
				clazz.addField(ctField, InitializerUtils.getInitializer(field.getType(), aggregationType));
			} else {
				clazz.addField(ctField);
			}

			final CtMethod getter = CtMethod.make(TemplateHelper.getter(fieldName, field.getType()), clazz);
			final CtMethod setter = CtMethod.make(TemplateHelper.setter(fieldName, field.getType()), clazz);

			clazz.addMethod(getter);
			clazz.addMethod(setter);
		});
	}

	public void updateStatistics(final Object statisticsData) {
		final Object statisticsBucket = getStatisticsBucket(statisticsData);

		updateStatistics(statisticsBucket, statisticsData);
	}

	protected void updateStatistics(final Object statisticsBucket, final Object statisticsData) {
		classInfo.getStatisticsUpdaters().forEach(handles -> {
			propogate(() -> {
				final Object value = handles.getLeft().invoke(statisticsData);

				handles.getRight().invoke(statisticsBucket, value);
			});
		});
	}

	protected Object getStatisticsBucket(final Object statisticsData) {
		final StatisticsKey key = generateStatisticsKey(statisticsData);

		Object statisticsBucket = statistics.get(key);

		if (statisticsBucket == null) {
			statisticsBucket = generateStatisticsBucket(key, statisticsData);
		}

		return statisticsBucket;
	}

	protected StatisticsKey generateStatisticsKey(final Object statisticsData) {
		final StatisticsKeyBuilder builder = new StatisticsKeyBuilder();

		classInfo.getStatisticsKeyHandles().forEach((name, handles) -> {
			propogate(() -> {
				final Object value = handles.getLeft().invoke(statisticsData);

				builder.withParameter(name, value);
			});
		});

		return builder.build();
	}

	protected Object generateStatisticsBucket(final StatisticsKey key, final Object statisticsData) {
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

	protected Map<StatisticsKey, Object> getStatistics() {
		return statistics;
	}
}
