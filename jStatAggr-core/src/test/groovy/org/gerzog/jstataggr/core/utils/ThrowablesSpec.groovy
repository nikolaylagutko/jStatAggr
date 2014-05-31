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

