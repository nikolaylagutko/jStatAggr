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
package org.gerzog.jstataggr.core.internal.impl

import org.gerzog.jstataggr.IStatisticsHandler
import org.gerzog.jstataggr.IStatisticsManager
import org.gerzog.jstataggr.core.StatisticsEntry

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
