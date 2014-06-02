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

import org.gerzog.jstataggr.core.manager.impl.StatisticsKey.StatisticsKeyBuilder

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsCollectorSpec extends Specification {

	StatisticsCollector collector = Spy(StatisticsCollector)

	Object statisticsData = new Object()

	Object statisticsBucket = new Object()

	StatisticsKey key = new StatisticsKeyBuilder().build()

	def "check collector creation on statistics update"() {
		when:
		collector.updateStatistics(statisticsData)

		then:
		1 * collector.getStatisticsBucket(statisticsData) >> statisticsBucket
		1 * collector.updateStatistics(statisticsBucket, statisticsData) >> null
	}

	def "check bucket found"() {
		setup:
		collector.statistics.put(key, statisticsBucket)

		when:
		def bucket = collector.getStatisticsBucket(statisticsData)

		then:
		1 * collector.generateStatisticsKey(statisticsData) >> key
		bucket != null
	}

	def "check bucket not found and generated"() {
		when:
		def bucket = collector.getStatisticsBucket(statisticsData)

		then:
		1 * collector.generateStatisticsKey(statisticsData) >> key
		1 * collector.generateStatisticsBucket(key, statisticsData) >> statisticsBucket
		bucket != null
	}
}
