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
package org.gerzog.jstataggr.core.utils

import org.gerzog.jstataggr.core.utils.Throwables.Callable
import org.gerzog.jstataggr.core.utils.Throwables.VoidCallable

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ThrowablesSpec extends Specification {

	VoidCallable callableWithoutError = { int i = 0 }
	Callable callableWithRuntimeError = { throw new NullPointerException() }
	Callable callableWithError = { throw new IOException() }

	def "check no exception"() {
		when:
		Throwables.propogate(callableWithoutError)

		then:
		noExceptionThrown()
	}

	def "check runtime exception"() {
		when:
		Throwables.propogate(callableWithRuntimeError)

		then:
		thrown(NullPointerException)
	}

	def "check non-runtime exception"() {
		when:
		Throwables.propogate(callableWithError)

		then:
		def e = thrown(RuntimeException)
		e.cause instanceof IOException
	}

	def "check exception handling with re-throwing exception"() {
		when:
		Throwables.propogate(callableWithError, { e ->
			new IllegalArgumentException(e)
		})

		then:
		thrown(IllegalArgumentException)
	}
}

