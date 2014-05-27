package org.gerzog.jstataggr.core.impl

import java.util.concurrent.ExecutorService

import org.gerzog.jstataggr.core.IStatisticsHandler

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AsyncStatisticsHandlerSpec extends Specification {

	IStatisticsHandler handler = new AsyncStatisticsHandler()

	Runnable action = Mock(Runnable)

	ExecutorService executor = Mock(ExecutorService)

	def setup() {
		handler.threadNumber = 5
	}

	def "check executor initialized"() {
		when:
		handler.initialize()

		then:
		executor != null
	}

	def "check thread number is negative"() {
		setup:
		handler.threadNumber = -1

		when:
		handler.initialize()

		then:
		thrown(IllegalArgumentException)
	}

	def "check thread number is zero"() {
		setup:
		handler.threadNumber = 0

		when:
		handler.initialize()

		then:
		thrown(IllegalArgumentException)
	}

	def "check handling with uninitialized executor"() {
		when:
		handler.handleStatistics(action)

		then:
		thrown(NullPointerException)
	}

	def "check handling with initialized executor"() {
		setup:
		handler.updateExecutor = executor

		when:
		handler.handleStatistics(action)

		then:
		1 * executor.submit(action)
	}
}
