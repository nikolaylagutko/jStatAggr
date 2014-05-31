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

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.gerzog.jstataggr.core.AggregationType;
import org.gerzog.jstataggr.core.manager.impl.StatisticsKey.StatisticsKeyBuilder;
import org.gerzog.jstataggr.core.templates.TemplateHelper;

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

	private class CollectorClassInfo {
		private Class<?> collectorClass;

		private Map<String, Pair<MethodHandle, MethodHandle>> statisticsKeyHandles;

		private List<Pair<MethodHandle, MethodHandle>> statisticsUpdaters;
	}

	private CollectorClassInfo classInfo;

	private final Map<StatisticsKey, Object> statistics = new ConcurrentHashMap<>();

	private StatisticsCollector() {

	}

	protected static CollectorClassInfo generateClassInfo(final String className, final Map<Field, MethodHandle> statisticsKeys, final Map<Field, MethodHandle> aggregations, final Map<Field, List<AggregationType>> aggregationTypes) {
		final ClassPool pool = ClassPool.getDefault();

		final CtClass clazz = pool.makeClass(PACKAGE_PREFIX + StringUtils.capitalize(className));

		addProperties(clazz, statisticsKeys.keySet(), pool);
		addProperties(clazz, aggregations.keySet(), pool);
		addUpdaters(clazz, aggregations.keySet(), aggregationTypes, pool);

		return null;
	}

	protected static void addProperties(final CtClass clazz, final Set<Field> fields, final ClassPool pool) {
		fields.forEach(field -> addProperty(clazz, field, pool));
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
		try {
			final CtMethod updater = CtMethod.make(TemplateHelper.simpleUpdater(field.getName(), field.getType(), aggregation), clazz);

			clazz.addMethod(updater);
		} catch (final CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}

	protected static void addProperty(final CtClass clazz, final Field field, final ClassPool pool) {
		try {
			final CtClass type = pool.getCtClass(field.getType().getName());

			final CtField ctField = new CtField(type, field.getName(), clazz);
			clazz.addField(ctField);

			final CtMethod getter = CtMethod.make(TemplateHelper.getter(field.getName(), field.getType()), clazz);
			final CtMethod setter = CtMethod.make(TemplateHelper.setter(field.getName(), field.getType()), clazz);

			clazz.addMethod(getter);
			clazz.addMethod(setter);
		} catch (final NotFoundException | CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateStatistics(final Object statisticsData) {
		final Object statisticsPiece = getStatisticsPiece(statisticsData);

		classInfo.statisticsUpdaters.forEach(handles -> {
			try {
				final Object value = handles.getLeft().invoke(statisticsData);

				handles.getRight().invoke(statisticsPiece, value);
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	private Object getStatisticsPiece(final Object statisticsData) {
		final StatisticsKey key = generateStatisticsKey(statisticsData);

		Object statisticsPiece = statistics.get(key);

		if (statisticsPiece == null) {
			statisticsPiece = generateStatisticsCollector(key, statisticsData);
		}

		return statisticsPiece;
	}

	protected StatisticsKey generateStatisticsKey(final Object statisticsData) {
		final StatisticsKeyBuilder builder = new StatisticsKeyBuilder();

		classInfo.statisticsKeyHandles.forEach((name, handles) -> {
			try {
				final Object value = handles.getLeft().invoke(statisticsData);

				builder.withParameter(name, value);
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		});

		return builder.build();
	}

	protected Object generateStatisticsCollector(final StatisticsKey key, final Object statisticsData) {
		try {
			final Object result = classInfo.collectorClass.newInstance();

			classInfo.statisticsKeyHandles.forEach((name, handles) -> {
				try {
					final Object value = key.get(name);

					handles.getRight().invoke(result, value);
				} catch (final Throwable e) {
					throw new RuntimeException();
				}
			});

			final Object existing = statistics.putIfAbsent(key, result);

			return existing == null ? result : existing;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
