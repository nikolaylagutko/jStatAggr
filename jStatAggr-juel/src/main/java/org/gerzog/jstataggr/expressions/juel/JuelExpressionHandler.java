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
package org.gerzog.jstataggr.expressions.juel;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Map;
import java.util.Map.Entry;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.gerzog.jstataggr.core.expressions.IExpressionHandler;

import de.odysseus.el.util.SimpleContext;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class JuelExpressionHandler implements IExpressionHandler {

	private ExpressionFactory expressionFactory;

	private Map<String, Object> contextBeans;

	private SimpleContext context;

	public void setExpressionFactory(final ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
	}

	public void setContextBeans(final Map<String, Object> contextBeans) {
		this.contextBeans = contextBeans;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invokeExpression(final String expression, final T originalValue) throws Exception {
		notNull(originalValue, "Context value cannot be null");
		notEmpty(expression, "Expression cannot be null or empty");

		addVariable("this", originalValue);

		return (T) expressionFactory.createValueExpression(getContext(), "${" + expression + "}", originalValue.getClass()).getValue(getContext());
	}

	public void initialize() {
		notNull(expressionFactory, "ExpressionFactory is null");

		for (final Entry<String, Object> contextBean : contextBeans.entrySet()) {
			addVariable(contextBean.getKey(), contextBean.getValue());
		}
	}

	private void addVariable(final String name, final Object value) {
		final ValueExpression expression = expressionFactory.createValueExpression(value, value.getClass());

		getContext().setVariable(name, expression);
	}

	private SimpleContext getContext() {
		if (context == null) {
			context = createContext();
		}

		return context;
	}

	protected SimpleContext createContext() {
		return new SimpleContext();
	}
}
