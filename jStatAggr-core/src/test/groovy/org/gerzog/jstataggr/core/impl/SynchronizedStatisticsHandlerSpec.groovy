package org.gerzog.jstataggr.core.impl

import org.gerzog.jstataggr.core.IStatisticsHandler

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class SynchronizedStatisticsHandlerSpec extends Specification {

	IStatisticsHandler handler = new SynchronizedStatisticsHandler()

	Runnable action = Mock(Runnable)

	def "check workflow"() {
		when:
		handler.handleStatistics(action)

		then:
		action.run()
	}
}
