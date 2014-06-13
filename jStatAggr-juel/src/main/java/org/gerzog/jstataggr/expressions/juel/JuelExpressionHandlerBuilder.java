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

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;

import org.apache.commons.lang3.StringUtils;
import org.gerzog.jstataggr.core.expressions.IExpressionHandler;

public final class JuelExpressionHandlerBuilder {

	private final JuelExpressionHandler result;

	private final Map<String, Object> contextBeans = new HashMap<>();

	private JuelExpressionHandlerBuilder(final JuelExpressionHandler handler) {
		this.result = handler;
	}

	private JuelExpressionHandlerBuilder() {
		this(new JuelExpressionHandler());
	}

	@SuppressWarnings("unused")
	private static JuelExpressionHandlerBuilder test(final JuelExpressionHandler handler) {
		return new JuelExpressionHandlerBuilder(handler);
	}

	public static JuelExpressionHandlerBuilder newBuilder() {
		return newBuilder(ExpressionFactory.newInstance());
	}

	public static JuelExpressionHandlerBuilder newBuilder(final ExpressionFactory expressionFactory) {
		final JuelExpressionHandlerBuilder builder = new JuelExpressionHandlerBuilder();

		builder.result.setExpressionFactory(expressionFactory);

		return builder;
	}

	public JuelExpressionHandlerBuilder registerBean(final Object bean) {
		return registerBean(bean, StringUtils.uncapitalize(bean.getClass().getSimpleName()));
	}

	public JuelExpressionHandlerBuilder registerBean(final Object bean, final String name) {
		notNull(bean, "Cannot register null as context bean");
		notNull(name, "Cannot register null as context bean name");
		isTrue(!contextBeans.containsKey(name), "Context already contains bean named <%s>", name);

		contextBeans.put(name, bean);

		return this;
	}

	public IExpressionHandler build() {
		result.setContextBeans(contextBeans);
		result.initialize();

		return result;
	}

}
