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
package org.gerzog.jstataggr.expressions.spel

import org.gerzog.jstataggr.core.expressions.IExpressionHandler
import org.gerzog.jstataggr.expressions.config.TestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@ContextConfiguration(classes = [TestContext.class])
class SpelExpressionHandlerSpec extends Specification {

	@Autowired
	IExpressionHandler handler

	@Unroll
	def "check expression handling"(def expression, def value, def expected) {
		when:
		def result = handler.invokeExpression(expression, value)

		then:
		result == expected

		where:
		expression 				| value | expected
		'1 + 1'					| 10	| 2
		'#this + 5'				| 10	| 15
		'#this + @bean.value'	| 10	| 20
	}

	def "check an error occured when value is null"() {
		when:
		handler.invokeExpression('1 + 1', null)

		then:
		thrown(NullPointerException)
	}

	def "check an error occured when expression is null or empty"(def expression, def exception) {
		when:
		handler.invokeExpression(expression, 10)

		then:
		thrown(exception)

		where:
		expression | exception
		null	   | NullPointerException
		''		   | IllegalArgumentException
	}
}
