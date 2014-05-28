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

	def "check executor stopped on shutdown"() {
		setup:
		handler.updateExecutor = executor

		when:
		handler.shutdown()

		then:
		1 * executor.shutdown()
	}
}
