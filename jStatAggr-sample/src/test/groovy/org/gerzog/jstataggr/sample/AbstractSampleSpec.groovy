package org.gerzog.jstataggr.sample

import org.gerzog.jstataggr.IStatisticsHandler
import org.gerzog.jstataggr.IStatisticsManager
import org.gerzog.jstataggr.core.manager.impl.StatisticsManagerImpl

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
abstract class AbstractSampleSpec extends Specification {

	IStatisticsHandler handler

	IStatisticsManager manager

	def setup() {
		manager = new StatisticsManagerImpl()

		handler = createHandler(manager)
	}

	def "check statistics handling"() {
		setup:
		def values = generateStatistics()

		when:
		values.each { handler.handleStatistics(it) }

		then:
		noExceptionThrown()
	}

	abstract IStatisticsHandler createHandler(IStatisticsManager manager)

	private List<Object> generateStatistics() {
		List<Object> result = new ArrayList<>()

		result.addAll(generateStatisticsOne())
		result.addAll(generateStatisticsTwo())

		result
	}

	private List<StatisticsOne> generateStatisticsOne() {
		int counter = 0
		def result = []

		2.times { key1 ->
			3.times { key2 ->
				10.times {
					StatisticsOne statistics = new StatisticsOne()
					statistics.key = key1.toString()
					statistics.anotherKey = key2.toString()
					statistics.value = counter++

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
