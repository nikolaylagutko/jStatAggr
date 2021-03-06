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
import org.gerzog.jstataggr.el.test.AbstractExpressionHandlerSpec
import org.gerzog.jstataggr.expressions.config.TestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@ContextConfiguration(classes = [TestContext.class])
class SpelExpressionHandlerSpec extends AbstractExpressionHandlerSpec {

	@Autowired
	IExpressionHandler handler


	@Override
	public IExpressionHandler getExpressionHandler() {
		handler
	}

	@Override
	public String getThis() {
		'#this'
	}

	@Override
	public String getBean(String beanName) {
		"@${beanName}"
	}
}
