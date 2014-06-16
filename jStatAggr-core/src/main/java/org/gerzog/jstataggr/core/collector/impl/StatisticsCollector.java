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
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.FieldType;
import org.gerzog.jstataggr.IStatisticsFilter;
import org.gerzog.jstataggr.IStatisticsKey;
import org.gerzog.jstataggr.core.collector.impl.StatisticsKey.StatisticsKeyBuilder;
import org.gerzog.jstataggr.core.expressions.IExpressionHandler;
import org.gerzog.jstataggr.core.manager.impl.internal.IStatisticsField;
import org.gerzog.jstataggr.core.manager.impl.internal.StatisticsFields;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class StatisticsCollector {

	private static final String PACKAGE_PREFIX = "org.gerzog.jstataggr.core.manager.impl.generated.";

	private static class FieldInfo {

		private final String name;

		private final String expression;

		private final MethodHandle getter;

		private MethodHandle accessor;

		public FieldInfo(final String name, final String expression,
				final MethodHandle getter) {
			this.name = name;
			this.expression = expression;
			this.getter = getter;
		}

		public void setAccessor(final MethodHandle accessor) {
			this.accessor = accessor;
		}

		public String getName() {
			return name;
		}

		public String getExpression() {
			return expression;
		}

		public MethodHandle getGetter() {
			return getter;
		}

		public MethodHandle getAccessor() {
			return accessor;
		}
	}

	public static class StatisticsCollectorBuilder {

		private final String className;

		private final StatisticsCollector result;

		private final Map<IStatisticsField, FieldInfo> statisticsFieldInfo = new HashMap<>();

		public StatisticsCollectorBuilder(final String className,
				final IExpressionHandler expressionHandler) {
			this.result = new StatisticsCollector(expressionHandler);
			this.className = className;
		}

		public StatisticsCollectorBuilder addStatisticsKey(final Field field,
				final MethodHandle getter, final String expression) {

			add(field.getName(),
					expression,
					StatisticsFields.forStatisticsKey(field.getName(),
							field.getType()), getter);

			return this;
		}

		public StatisticsCollectorBuilder addAggregation(final Field field,
				final AggregationType[] aggregationTypes,
				final FieldType fieldType, final MethodHandle getter,
				final String expression) {
			for (final AggregationType type : aggregationTypes) {
				add(field.getName(),
						expression,
						StatisticsFields.forAggregation(field.getName(),
								field.getType(), type, fieldType), getter);
			}

			return this;
		}

		private StatisticsCollectorBuilder add(final String name,
				final String expression,
				final IStatisticsField statisticsField,
				final MethodHandle getter) {
			final FieldInfo fieldInfo = new FieldInfo(name, expression, getter);

			statisticsFieldInfo.put(statisticsField, fieldInfo);

			return this;
		}

		public StatisticsCollector build() {
			result.classInfo = generateClassInfo(className, statisticsFieldInfo);

			return result;
		}

	}

	private static class CollectorClassInfo {
		private final Class<?> bucketClass;

		private Class<?> externalEntityClass;

		private final Map<String, FieldInfo> keys = new HashMap<>();

		private final List<FieldInfo> updaters = new ArrayList<>();

		private BeanCopier externalEntityConverter;

		public CollectorClassInfo(final Class<?> bucketClass) {
			this.bucketClass = bucketClass;
		}

		public BeanCopier getExternalEntityConverter() {
			return externalEntityConverter;
		}

		public Class<?> getBucketClass() {
			return bucketClass;
		}

		public Map<String, FieldInfo> getKeys() {
			return keys;
		}

		public List<FieldInfo> getUpdaters() {
			return updaters;
		}

		public Class<?> getExternalEntityClass() {
			return externalEntityClass;
		}

		public void setExternalEntityClass(final Class<?> externalEntityClass) {
			this.externalEntityClass = externalEntityClass;
			this.externalEntityConverter = BeanCopier.create(bucketClass,
					externalEntityClass, false);
		}

	}

	private CollectorClassInfo classInfo;

	private final Map<IStatisticsKey, Object> statistics = new ConcurrentHashMap<>();

	private final IExpressionHandler expressionHandler;

	private final Transformer<Object, Object> exportTransformer = (input) -> convert(input);

	// LN: 2.06.2014, made package-visible for tests
	StatisticsCollector(final IExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	private Object convert(final Object input) {
		return propogate(() -> {
			final Object externalEntity = classInfo.getExternalEntityClass()
					.newInstance();

			classInfo.getExternalEntityConverter().copy(input, externalEntity,
					null);

			return externalEntity;
		});
	}

	protected static CollectorClassInfo generateClassInfo(
			final String className,
			final Map<IStatisticsField, FieldInfo> fieldInfo) {
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
			final Map<IStatisticsField, FieldInfo> fieldInfo) {
		final CollectorClassInfo result = new CollectorClassInfo(clazz);
		final Map<String, Class<?>> methodInfo = new HashMap<>();

		fieldInfo.forEach((statisticsInfo, bucketInfo) -> {
			propogate(() -> {
				final MethodHandle accessMethod = statisticsInfo
						.getAccessMethodHandle(clazz);

				bucketInfo.setAccessor(accessMethod);

				if (statisticsInfo.isAggregator()) {
					result.getUpdaters().add(bucketInfo);
				} else {
					result.getKeys().put(bucketInfo.getName(), bucketInfo);
				}

				methodInfo.put(statisticsInfo.getFieldName(),
						statisticsInfo.getMethodType());
			});
		});

		final BeanGenerator externalBeanGenerator = new BeanGenerator();
		BeanGenerator.addProperties(externalBeanGenerator, methodInfo);

		result.setExternalEntityClass((Class<?>) externalBeanGenerator
				.createClass());

		return result;
	}

	public void updateStatistics(final Object statisticsData) {
		final Object statisticsBucket = getStatisticsBucket(statisticsData);

		updateStatistics(statisticsBucket, statisticsData);
	}

	protected void updateStatistics(final Object statisticsBucket,
			final Object statisticsData) {
		classInfo.getUpdaters().forEach(updater -> {
			propogate(() -> {
				Object value = updater.getGetter().invoke(statisticsData);

				value = updateValue(value, updater.getExpression());

				updater.getAccessor().invoke(statisticsBucket, value);
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

		classInfo.getKeys().forEach((name, handles) -> {
			propogate(() -> {
				Object value = handles.getGetter().invoke(statisticsData);

				value = updateValue(value, handles.getExpression());

				builder.withParameter(name, value);
			});
		});

		return builder.build();
	}

	protected Object generateStatisticsBucket(final IStatisticsKey key,
			final Object statisticsData) {
		return propogate(() -> {
			final Object result = classInfo.getBucketClass().newInstance();

			classInfo.getKeys().forEach((name, handles) -> {
				propogate(() -> {
					final Object value = key.get(name);

					handles.getAccessor().invoke(result, value);
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

		return CollectionUtils
				.transformingCollection(result, exportTransformer);
	}

	protected Object updateValue(final Object original, final String expression)
			throws Exception {
		if ((expressionHandler != null) && (expression != null)) {
			return expressionHandler.invokeExpression(expression, original);
		}

		return original;
	}
}
