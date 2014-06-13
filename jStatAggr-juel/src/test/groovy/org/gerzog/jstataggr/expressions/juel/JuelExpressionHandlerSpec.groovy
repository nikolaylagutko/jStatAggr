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

import javax.el.ExpressionFactory
import javax.el.ValueExpression

import org.gerzog.jstataggr.core.expressions.IExpressionHandler
import org.gerzog.jstataggr.el.test.AbstractExpressionHandlerSpec
import org.gerzog.jstataggr.el.test.TestBean

import de.odysseus.el.util.SimpleContext

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class JuelExpressionHandlerSpec extends AbstractExpressionHandlerSpec {

	static final OBJECT = 'some object'

	static final OBJECT_NAME = 'some_object_name'

	IExpressionHandler realHandler

	IExpressionHandler mockedHandler

	ExpressionFactory factory = Mock(ExpressionFactory)

	SimpleContext context = Mock(SimpleContext)

	def setup() {
		realHandler = JuelExpressionHandlerBuilder.newBuilder().registerBean(new TestBean(), "bean").build()

		mockedHandler = Spy(JuelExpressionHandler)
		mockedHandler.expressionFactory = factory
		mockedHandler.createContext() >> context
	}

	@Override
	public IExpressionHandler getExpressionHandler() {
		realHandler
	}

	def "check handler initialization"() {
		setup:
		def contextBeans = [(OBJECT_NAME) : OBJECT]
		mockedHandler.setContextBeans(contextBeans)

		when:
		mockedHandler.initialize()

		then:
		1 * factory.createValueExpression(OBJECT, String) >> Mock(ValueExpression)
		1 * context.setVariable(OBJECT_NAME, _ as ValueExpression)
	}

	def "check uninitialized expression factory"() {
		setup:
		mockedHandler.expressionFactory = null

		when:
		mockedHandler.initialize()

		then:
		thrown(NullPointerException)
	}

	@Override
	public String getThis() {
		'this'
	}

	@Override
	public String getBean(String beanName) {
		beanName
	}
}
