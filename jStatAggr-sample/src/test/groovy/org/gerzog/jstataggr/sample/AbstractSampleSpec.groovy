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
			if (statisticsName == 'sample_statistics_one') {
				isCalled = true
				this.statisticsName = statisticsName
				this.data = statisticsData
			}
		}

		public void verify() {
			assert isCalled
			assert data.size() == 6

			data.each {
				assert it.valueMax == 900
				assert it.valueMin == 0
				assert it.valueSum == [0..9].sum().sum() * 100
				assert it.getValueAverage() == 450
			}
		}
	}

	class StatisticsTwoVerifier implements IStatisticsWriter {

		private boolean isCalled = false

		private String statisticsName

		private Collection<Object> data

		@Override
		public void writeStatistics(String statisticsName,
				Collection<Object> statisticsData) {
			if (statisticsName == 'sample_statistics_two') {
				isCalled = true
				this.statisticsName = statisticsName
				this.data = statisticsData
			}
		}

		public void verify() {
			assert isCalled
			assert data.size() == 6

			int size = [0..9].sum().sum()

			data.each {
				assert it.value1Count == size
				assert it.value2Count == size
				assert it.value3Count == 10
			}
		}
	}

	class StatisticsThreeVerifier implements IStatisticsWriter {

		private boolean isCalled = false

		private String statisticsName

		private Collection<Object> data

		@Override
		public void writeStatistics(String statisticsName,
				Collection<Object> statisticsData) {
			if (statisticsName == 'sample_statistics_three') {
				isCalled = true
				this.statisticsName = statisticsName
				this.data = statisticsData
			}
		}

		public void verify() {
			assert isCalled
			assert data.size() == 6

			def sum = [0..9].sum().sum() * 100

			data.each {
				//verify average
				assert it.value1Average == 450
				assert it.value2Average == 450

				//verify count
				assert it.value1Count == 10
				assert it.value2Count == 10
				assert it.value3Count == 10

				//verify max
				assert it.value1Max == 900
				assert it.value2Max == 900
				assert it.value3Max == 900

				//verify min
				assert it.value1Min == 0
				assert it.value2Min == 0
				assert it.value3Min == 0

				//verify sum
				assert it.value1Sum == sum
				assert it.value2Sum == sum
				assert it.value3Sum == sum
			}
		}
	}

	IStatisticsHandler handler

	IStatisticsManager manager

	IStatisticsWriter statisticsOneVerifier = new StatisticsOneVerifier()

	IStatisticsWriter statisticsTwoVerifier = new StatisticsTwoVerifier()

	IStatisticsWriter statisticsThreeVerifier = new StatisticsThreeVerifier()

	def setup() {
		manager = new StatisticsManagerImpl()

		handler = createHandler(manager)
		handler.statisticsWriters = [
			statisticsOneVerifier,
			statisticsTwoVerifier,
			statisticsThreeVerifier
		]
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
		statisticsTwoVerifier.verify()
		statisticsThreeVerifier.verify()
	}

	abstract IStatisticsHandler createHandler(IStatisticsManager manager)

	private List<Object> generateStatistics() {
		List<Object> result = new ArrayList<>()

		result.addAll(generateStatisticsOne())
		result.addAll(generateStatisticsTwo())
		result.addAll(generateStatisticsThree())

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
					statistics.value = it * 100

					result << statistics
				}
			}
		}

		result
	}

	private List<StatisticsThree> generateStatisticsThree() {
		def result = []

		2.times { key1 ->
			3.times { key2 ->
				10.times {
					StatisticsThree statistics = new StatisticsThree()
					statistics.key = key1.toString()
					statistics.anotherKey = key2.toString()
					statistics.value1 = it * 100
					statistics.value2 = it * 100
					statistics.value3 = it * 100

					result << statistics
				}
			}
		}

		result
	}

	private List<StatisticsTwo> generateStatisticsTwo() {
		def result = []

		2.times { key1 ->
			3.times { key2 ->
				10.times {
					StatisticsTwo statistics = new StatisticsTwo()

					statistics.key = key1.toString()
					statistics.anotherKey = key2

					statistics.value1 = new int[it]
					statistics.value2 = []

					it.times {
						statistics.value1[it] = it
						statistics.value2 == null ? statistics.value2 = [it]: statistics.value2 << it
					}
					statistics.value3 = it

					result << statistics
				}
			}
		}

		result
	}
}
