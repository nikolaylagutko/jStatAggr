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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.IStatisticsFilter;
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

	public static class FieldInfo {

		private final AggregationType aggregation;

		private final Class<?> fieldType;

		private final MethodHandle getterMethod;

		private final boolean isAggregation;

		private final String fieldName;

		public FieldInfo(final String fieldName, final Class<?> fieldType, final MethodHandle getterMethod) {
			this(fieldName, fieldType, null, getterMethod);
		}

		public FieldInfo(final String fieldName, final Class<?> fieldType, final AggregationType aggregation, final MethodHandle getterMethod) {
			this.fieldName = fieldName;
			this.getterMethod = getterMethod;
			this.aggregation = aggregation;
			this.fieldType = fieldType;
			this.isAggregation = aggregation != null;
		}

		public AggregationType getAggregation() {
			return aggregation;
		}

		public Class<?> getFieldType() {
			return fieldType;
		}

		public MethodHandle getGetterMethod() {
			return getterMethod;
		}

		public boolean isAggregation() {
			return isAggregation;
		}

		public String getFieldName() {
			return fieldName;
		}

	}

	public static class StatisticsCollectorBuilder {

		private final String className;

		private final StatisticsCollector result = new StatisticsCollector();

		private final List<FieldInfo> fieldInfo = new ArrayList<>();

		public StatisticsCollectorBuilder(final String className) {
			this.className = className;
		}

		public StatisticsCollectorBuilder addStatisticsKey(final Field field, final MethodHandle getter) {
			fieldInfo.add(new FieldInfo(field.getName(), field.getType(), getter));

			return this;
		}

		public StatisticsCollectorBuilder addAggregation(final Field field, final AggregationType[] aggregationTypes, final MethodHandle getter) {
			for (final AggregationType type : aggregationTypes) {
				fieldInfo.add(new FieldInfo(field.getName(), field.getType(), type, getter));
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

	private final Map<StatisticsKey, Object> statistics = new ConcurrentHashMap<>();

	// LN: 2.06.2014, made package-visible for tests
	StatisticsCollector() {

	}

	protected static CollectorClassInfo generateClassInfo(final String className, final List<FieldInfo> fieldInfo) {
		final ClassPool pool = ClassPool.getDefault();

		final CtClass clazz = pool.makeClass(PACKAGE_PREFIX + StringUtils.capitalize(className));

		appendAverageAggregations(fieldInfo);

		fieldInfo.forEach(info -> {
			addProperty(clazz, info, pool);

			if (info.isAggregation() && (info.getAggregation() != AggregationType.AVERAGE)) {
				addUpdater(clazz, info, pool);
			}
		});

		return propogate(() -> {
			return generateClassInfo(clazz.toClass(), fieldInfo);
		});
	}

	protected static void appendAverageAggregations(final List<FieldInfo> fieldInfo) {
		final Map<String, Map<AggregationType, FieldInfo>> detailedFieldInfo = new HashMap<>();

		fieldInfo.forEach(info -> {
			Map<AggregationType, FieldInfo> data = detailedFieldInfo.get(info.getFieldName());

			if (data == null) {
				data = new HashMap<>();
				detailedFieldInfo.put(info.getFieldName(), data);
			}

			data.put(info.getAggregation(), info);
		});

		detailedFieldInfo.forEach((name, data) -> {
			final FieldInfo averageData = data.get(AggregationType.AVERAGE);
			if (averageData != null) {
				appendFieldInfo(averageData, AggregationType.SUM, data, fieldInfo);
				appendFieldInfo(averageData, AggregationType.COUNT, data, fieldInfo);
			}
		});

		fieldInfo.sort((first, second) -> {
			if (first.getAggregation() == AggregationType.AVERAGE) {
				return 1;
			} else if (second.getAggregation() == AggregationType.AVERAGE) {
				return -1;
			}

			return 0;
		});
	}

	private static void appendFieldInfo(final FieldInfo template, final AggregationType type, final Map<AggregationType, FieldInfo> dataMap, final List<FieldInfo> fieldInfo) {
		final FieldInfo info = dataMap.get(type);

		if (info == null) {
			fieldInfo.add(new FieldInfo(template.getFieldName(), template.getFieldType(), type, template.getGetterMethod()));
		}
	}

	protected static CollectorClassInfo generateClassInfo(final Class<?> clazz, final List<FieldInfo> fieldInfo) {
		final CollectorClassInfo result = new CollectorClassInfo(clazz);

		fieldInfo.forEach((info) -> {
			propogate(() -> {
				if (info.getAggregation() != AggregationType.AVERAGE) {
					final String fieldName = info.getFieldName();
					final String methodName = info.isAggregation() ? FieldUtils.getUpdaterName(fieldName, info.getAggregation()) : FieldUtils.getSetterName(info.getFieldName());

					final Method method = clazz.getMethod(methodName, info.getFieldType());
					final MethodHandle handle = MethodHandles.lookup().unreflect(method);

					if (info.isAggregation()) {
						result.getStatisticsUpdaters().add(ImmutablePair.of(info.getGetterMethod(), handle));
					} else {
						result.getStatisticsKeyHandles().put(fieldName, ImmutablePair.of(info.getGetterMethod(), handle));
					}
				}
			});
		});

		return result;
	}

	protected static void addUpdater(final CtClass clazz, final FieldInfo field, final ClassPool pool) {
		switch (field.getAggregation()) {
		case MIN:
		case MAX:
		case SUM:
		case COUNT:
			addSimpleUpdater(clazz, field, pool);
			break;
		default:
			throw new UnsupportedOperationException("Updater for <" + field.getAggregation() + "> aggregation type is not implemented yet");
		}
	}

	protected static void addSimpleUpdater(final CtClass clazz, final FieldInfo field, final ClassPool pool) {
		propogate(() -> {
			final CtMethod updater = CtMethod.make(TemplateHelper.simpleUpdater(field.getFieldName(), field.getFieldType(), field.getAggregation()), clazz);

			clazz.addMethod(updater);
		});
	}

	protected static void addProperty(final CtClass clazz, final FieldInfo field, final ClassPool pool) {
		propogate(() -> {
			final String fieldName = field.isAggregation() ? FieldUtils.getAggregationFieldName(field.getFieldName(), field.getAggregation()) : field.getFieldName();

			final CtClass type = pool.getCtClass(field.getFieldType().getName());

			if (field.getAggregation() == AggregationType.AVERAGE) {
				final CtMethod getter = CtMethod.make(TemplateHelper.averageGetter(field.getFieldName(), field.getFieldType()), clazz);

				clazz.addMethod(getter);
			} else {
				final CtField ctField = new CtField(type, fieldName, clazz);
				if (field.isAggregation()) {
					clazz.addField(ctField, InitializerUtils.getInitializer(field.getFieldType(), field.getAggregation()));
				} else {
					clazz.addField(ctField);
				}

				final CtMethod getter = CtMethod.make(TemplateHelper.getter(fieldName, field.getFieldType()), clazz);
				final CtMethod setter = CtMethod.make(TemplateHelper.setter(fieldName, field.getFieldType()), clazz);

				clazz.addMethod(getter);
				clazz.addMethod(setter);
			}
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

	public Collection<Object> collectStatistics(final IStatisticsFilter filter, final boolean cleanup) {
		final Collection<Object> result = new ArrayList<>();

		statistics.keySet().forEach(key -> {
			if (filter.isApplied(key)) {
				final Object data = cleanup ? statistics.remove(key) : statistics.get(key);

				result.add(data);
			}
		});

		return result;
	}
}
