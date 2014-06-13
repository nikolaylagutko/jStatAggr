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
package org.gerzog.jstataggr.el.test

import org.gerzog.jstataggr.core.expressions.IExpressionHandler

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
abstract class AbstractExpressionHandlerSpec extends Specification {

	@Unroll
	def "check expression handling"(def expression, def value, def expected) {
		when:
		def result = getExpressionHandler().invokeExpression(expression, value)

		then:
		result == expected

		where:
		expression 									| value | expected
		'1 + 1'										| 10	| 2
		"${getThis()} + 5"							| 10	| 15
		"${getThis()} + ${getBean('bean')}.value"	| 10	| 20
	}

	abstract String getThis()

	abstract String getBean(String beanName)

	def "check an error occured when value is null"() {
		when:
		getExpressionHandler().invokeExpression('1 + 1', null)

		then:
		thrown(NullPointerException)
	}

	def "check an error occured when expression is null or empty"(def expression, def exception) {
		when:
		getExpressionHandler().invokeExpression(expression, 10)

		then:
		thrown(exception)

		where:
		expression | exception
		null	   | NullPointerException
		''		   | IllegalArgumentException
	}

	abstract IExpressionHandler getExpressionHandler()
}
