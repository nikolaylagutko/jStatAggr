package org.gerzog.jstataggr.core.internal.impl

import org.gerzog.jstataggr.core.IStatisticsHandler
import org.gerzog.jstataggr.core.annotations.StatisticsEntry
import org.gerzog.jstataggr.core.internal.IStatisticsManager
import org.gerzog.jstataggr.core.internal.impl.AbstractStatisticsHandler

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AbstractStatisticsHandlerSpec extends Specification {

	class TestStatisticsHandler extends AbstractStatisticsHandler {

		protected void handleStatistics(Runnable action) {
			action.run()
		}
	}

	class NotAnnotated {
	}

	@StatisticsEntry
	class Annotated {
	}

	@StatisticsEntry('cool name')
	class AnnotatedWithName {
	}

	IStatisticsManager manager = Mock(IStatisticsManager)

	IStatisticsHandler handler = Spy(TestStatisticsHandler)

	def setup() {
		handler.setManager(manager)
	}

	def "check an exception thrown when input parameter is null"() {
		when:
		handler.handleStatistics(null)

		then:
		thrown(NullPointerException)
	}

	def "check an exception thrown when input parameter is not-annotated class"() {
		when:
		handler.handleStatistics(new NotAnnotated())

		then:
		thrown(IllegalArgumentException)
	}

	def "check statistics handling continues after successfull validation"() {
		setup:
		def entry = new Annotated()

		when:
		handler.handleStatistics(entry)

		then:
		1 * handler.handleStatistics(_ as Runnable)
		1 * manager.updateStatistics(entry, Annotated, 'Annotated')
	}

	def "check statistics handling continues with defined name after successfull validation"() {
		setup:
		def entry = new AnnotatedWithName()

		when:
		handler.handleStatistics(entry)

		then:
		1 * handler.handleStatistics(_ as Runnable)
		1 * manager.updateStatistics(entry, AnnotatedWithName, 'cool name')
	}

	def "check an exception thrown when statistcs manager is null"() {
		setup:
		handler.setManager(null)

		when:
		handler.handleStatistics(new Annotated())

		then:
		thrown(NullPointerException)
	}
}
