/*
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
package org.gerzog.jstataggr.core.manager.impl

import java.lang.invoke.MethodHandle

import org.gerzog.jstataggr.AggregationType
import org.gerzog.jstataggr.IStatisticsFilter
import org.gerzog.jstataggr.IStatisticsManager
import org.gerzog.jstataggr.annotations.Aggregated
import org.gerzog.jstataggr.annotations.Expression
import org.gerzog.jstataggr.annotations.StatisticsEntry
import org.gerzog.jstataggr.annotations.StatisticsKey
import org.gerzog.jstataggr.core.collector.impl.StatisticsCollector
import org.gerzog.jstataggr.core.collector.impl.StatisticsCollector.StatisticsCollectorBuilder
import org.gerzog.jstataggr.core.expressions.IExpressionHandler

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsManagerImplSpec extends Specification {

	@StatisticsEntry
	class Statistics {

		@StatisticsKey
		String name

		@StatisticsKey
		@Expression('2 + 3')
		String expression

		@Aggregated([AggregationType.MIN, AggregationType.SUM, AggregationType.COUNT, AggregationType.AVERAGE])
		int value

		@Aggregated([AggregationType.COUNT, AggregationType.AVERAGE])
		int value2

		@Aggregated([AggregationType.SUM, AggregationType.AVERAGE])
		int value3

		String escaped
	}

	@StatisticsEntry
	class StatisticsWithoutGetter {

		@Aggregated(AggregationType.AVERAGE)
		private int field
	}

	@StatisticsEntry
	class StatisticsKeyIsAggregated {

		@StatisticsKey
		@Aggregated(AggregationType.MIN)
		String field
	}

	@StatisticsEntry
	class UnsupportedSumAggregation {

		@Aggregated(AggregationType.SUM)
		String field
	}

	@StatisticsEntry
	class UnsupportedMinAggregation {

		@Aggregated(AggregationType.MIN)
		String field
	}

	@StatisticsEntry
	class UnsupportedMaxAggregation {

		@Aggregated(AggregationType.MAX)
		String field
	}

	@StatisticsEntry
	class UnsupportedAverageAggregation {

		@Aggregated(AggregationType.AVERAGE)
		String field
	}

	@StatisticsEntry
	class NoStatisticsKey {

		@Aggregated(AggregationType.MIN)
		String field
	}

	@StatisticsEntry
	class ExpressionWithoutHandler {

		@Aggregated(AggregationType.MIN)
		@Expression('2 + 3')
		int field
	}

	@StatisticsEntry
	class EmptyExpression {

		@Aggregated(AggregationType.MIN)
		@Expression('')
		int field
	}

	IExpressionHandler expressionHandler = Mock(IExpressionHandler)

	IStatisticsManager manager = Spy(StatisticsManagerImpl, constructorArgs: [expressionHandler])

	Class<?> clazz = Statistics

	Object statistics = new Statistics()

	StatisticsCollectorBuilder builder = Mock(StatisticsCollectorBuilder)

	String statisticsName

	static int statisticsIndex

	def setup() {
		statisticsName = "Stats${statisticsIndex}"

		statisticsIndex++
	}

	def "check collector created"() {
		when:
		manager.updateStatistics(statistics, statisticsName)

		then:
		1 * manager.createCollector(clazz, statisticsName)
	}

	def "check no collector duplications"() {
		when:
		def collector1 = manager.updateStatistics(statistics, statisticsName)
		def collector2 = manager.updateStatistics(statistics, statisticsName)

		then:
		collector1.is(collector2)
		1 * manager.createCollector(clazz, statisticsName)
	}

	def "check collector creation"() {
		when:
		manager.createCollector(clazz, statisticsName)

		then:
		1 * manager.initializeCollector(clazz, _ as StatisticsCollectorBuilder)
	}

	def "check collector initialization workflow"() {
		setup:
		def nameField = Statistics.getDeclaredField('name')
		def valueField = Statistics.getDeclaredField('value')
		def expressionField = Statistics.getDeclaredField('expression')

		when:
		manager.initializeCollector(clazz, builder)

		then:
		1 * builder.addStatisticsKey(nameField, _ as MethodHandle, null)
		1 * builder.addAggregation(valueField, [
			AggregationType.MIN,
			AggregationType.SUM,
			AggregationType.COUNT,
			AggregationType.AVERAGE
		], _ as MethodHandle, null)
		1 * builder.addStatisticsKey(expressionField, _ as MethodHandle, '2 + 3')
	}

	def "check exception if getter not exists"() {
		when:
		manager.initializeCollector(StatisticsWithoutGetter, builder)

		then:
		thrown(IllegalStateException)
	}

	def "check actions on statistics collection with applied filter"() {
		setup:
		def filter = Mock(IStatisticsFilter)
		def collector = Mock(StatisticsCollector)

		manager.collectors.put(statisticsName, collector)

		when:
		manager.collectStatistics(statisticsName, filter, false)

		then:
		1 * collector.collectStatistics(filter, false)
	}

	def "check actions on statistics collection with not-applied filter"() {
		setup:
		def filter = Mock(IStatisticsFilter)
		def collector = Mock(StatisticsCollector)

		manager.collectors.put(statisticsName, collector)

		when:
		manager.collectStatistics('another name', filter, false)

		then:
		0 * collector.collectStatistics(filter, false)
	}

	def "check validation failed if statisticskey is also marked as aggregated"() {
		when:
		manager.updateStatistics(new StatisticsKeyIsAggregated(), statisticsName)

		then:
		thrown(IllegalStateException)
	}

	@Unroll
	def "check unsupported field types in aggregation"(def clazz) {
		setup:
		def instance = clazz.newInstance()

		when:
		manager.updateStatistics(instance, statisticsName)

		then:
		thrown(IllegalStateException)

		where:
		clazz << [
			UnsupportedAverageAggregation,
			UnsupportedMaxAggregation,
			UnsupportedMinAggregation,
			UnsupportedSumAggregation
		]
	}

	def "check statistics type marked invalid if it has no statisticskeys"() {
		when:
		manager.updateStatistics(new NoStatisticsKey(), statisticsName)

		then:
		thrown(IllegalStateException)
	}

	def "check type is invalid when @Expression exists but expression handler is null"() {
		setup:
		manager.expressionHandler = null

		when:
		manager.updateStatistics(new ExpressionWithoutHandler(), statisticsName)

		then:
		thrown(IllegalStateException)
	}

	def "check type is invalid when expression is empty"() {
		when:
		manager.updateStatistics(new EmptyExpression(), statisticsName)

		then:
		thrown(IllegalArgumentException)
	}
}
