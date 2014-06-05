/**
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
package org.gerzog.jstataggr.expressions.spel;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.PostConstruct;

import org.gerzog.jstataggr.core.expressions.IExpressionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class SpelExpressionHandler implements IExpressionHandler {

	private BeanFactory beanFactory;

	private EvaluationContext context;

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public <T> T invokeExpression(final String expressions,
			final T originalValue) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@PostConstruct
	public void initialize() {
		notNull(beanFactory, "Cannot initialize SpEL without BeanFactory");

		final StandardEvaluationContext context = new StandardEvaluationContext();
		context.setBeanResolver(new BeanFactoryResolver(beanFactory));

		this.context = context;
	}
}
