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
package org.gerzog.jstataggr.expressions.juel

import spock.lang.Specification

/**
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class JuelExpressionHandlerBuilderSpec extends Specification {

	static final BEAN_NAME = 'bean_name'

	JuelExpressionHandler handler = Mock(JuelExpressionHandler)

	JuelExpressionHandlerBuilder builder = JuelExpressionHandlerBuilder.test(handler)

	def "check error when context bean is null"() {
		when:
		builder.registerBean(null)

		then:
		thrown(NullPointerException)
	}

	def "check error on duplicated bean"() {
		setup:
		def object = BEAN_NAME
		builder.registerBean(object)

		when:
		builder.registerBean(object)

		then:
		thrown(IllegalArgumentException)
	}

	def "check error when context bean is null (with name)"() {
		when:
		builder.registerBean(null, BEAN_NAME)

		then:
		thrown(NullPointerException)
	}

	def "check error when context bean name is null"() {
		when:
		builder.registerBean(BEAN_NAME, null)

		then:
		thrown(NullPointerException)
	}

	def "check error on duplicated bean (with name)"() {
		setup:
		builder.registerBean('some object1', BEAN_NAME)

		when:
		builder.registerBean('some object2', BEAN_NAME)

		then:
		thrown(IllegalArgumentException)
	}

	def "check build actions"() {
		when:
		def result = builder.build()

		then:
		1 * handler.setContextBeans(_ as Map<String, Object>)
		1 * handler.initialize()
		result == handler
	}
}
