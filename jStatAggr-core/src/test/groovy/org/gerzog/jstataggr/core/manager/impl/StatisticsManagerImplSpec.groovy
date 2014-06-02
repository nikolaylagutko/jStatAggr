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
import org.gerzog.jstataggr.IStatisticsManager
import org.gerzog.jstataggr.core.Aggregated
import org.gerzog.jstataggr.core.StatisticsEntry
import org.gerzog.jstataggr.core.StatisticsKey
import org.gerzog.jstataggr.core.manager.impl.StatisticsCollector.StatisticsCollectorBuilder

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsManagerImplSpec extends Specification {

	@StatisticsEntry
	class Statistics {

		@StatisticsKey
		String name

		@Aggregated([AggregationType.MIN, AggregationType.SUM])
		int value

		String escaped
	}

	@StatisticsEntry
	class StatisticsWithoutGetter {

		@Aggregated(AggregationType.AVERAGE)
		private String field
	}

	IStatisticsManager manager = Spy(StatisticsManagerImpl)

	Class<?> clazz = Statistics.class

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
		manager.updateStatistics(statistics, clazz, statisticsName)

		then:
		1 * manager.createCollector(clazz, statisticsName)
	}

	def "check no collector duplications"() {
		when:
		def collector1 = manager.updateStatistics(statistics, clazz, statisticsName)
		def collector2 = manager.updateStatistics(statistics, clazz, statisticsName)

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

		when:
		manager.initializeCollector(clazz, builder)

		then:
		1 * builder.addStatisticsKey(nameField, _ as MethodHandle)
		1 * builder.addAggregation(valueField, [
			AggregationType.MIN,
			AggregationType.SUM
		], _ as MethodHandle)
	}

	def "check exception if getter not exists"() {
		when:
		manager.initializeCollector(StatisticsWithoutGetter, builder)

		then:
		thrown(IllegalStateException)
	}
}
