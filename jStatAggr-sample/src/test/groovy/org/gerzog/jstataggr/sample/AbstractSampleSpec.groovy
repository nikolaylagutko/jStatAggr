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
package org.gerzog.jstataggr.sample

import org.gerzog.jstataggr.IStatisticsHandler
import org.gerzog.jstataggr.IStatisticsManager
import org.gerzog.jstataggr.IStatisticsWriter
import org.gerzog.jstataggr.core.manager.impl.StatisticsManagerImpl

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
abstract class AbstractSampleSpec extends Specification {

	class StatisticsOneVerifier implements IStatisticsWriter {

		private boolean isCalled = false

		private String statisticsName

		private Collection<Object> data

		@Override
		public void writeStatistics(String statisticsName,
				Collection<Object> statisticsData) {
			isCalled = true
			this.statisticsName = statisticsName
			this.data = statisticsData
		}

		public void verify() {
			assert isCalled
			assert data.size() == 6

			data.each {
				assert it.valueMax == 9
				assert it.valueMin == 0
				assert it.valueSum == [0..9].sum().sum()
			}
		}
	}

	IStatisticsHandler handler

	IStatisticsManager manager

	IStatisticsWriter statisticsOneVerifier = new StatisticsOneVerifier()

	def setup() {
		manager = new StatisticsManagerImpl()

		handler = createHandler(manager)
		handler.statisticsWriters = [statisticsOneVerifier]
	}

	def "check statistics handling"() {
		setup:
		def values = generateStatistics()

		when:
		values.each { handler.handleStatistics(it) }
		and:
		handler.writeStatistics(true)

		then:
		noExceptionThrown()
		statisticsOneVerifier.verify()
	}

	abstract IStatisticsHandler createHandler(IStatisticsManager manager)

	private List<Object> generateStatistics() {
		List<Object> result = new ArrayList<>()

		result.addAll(generateStatisticsOne())
		result.addAll(generateStatisticsTwo())

		result
	}

	private List<StatisticsOne> generateStatisticsOne() {
		def result = []

		2.times { key1 ->
			3.times { key2 ->
				10.times {
					StatisticsOne statistics = new StatisticsOne()
					statistics.key = key1.toString()
					statistics.anotherKey = key2.toString()
					statistics.value = it

					result << statistics
				}
			}
		}

		result
	}

	private List<StatisticsOne> generateStatisticsTwo() {
		def result = []

		result
	}
}