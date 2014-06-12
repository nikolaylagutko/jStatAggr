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
package org.gerzog.jstataggr.core.manager.impl.internal;

import javassist.CtClass;
import javassist.CtMethod;

import org.gerzog.jstataggr.core.templates.TemplateHelper;
import org.gerzog.jstataggr.core.utils.FieldUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsField extends AbstractStatisticsField {

	public StatisticsField(final String originalFieldName,
			final Class<?> dataType) {
		super(originalFieldName, dataType);
	}

	@Override
	protected String getAccessMethodName() {
		return FieldUtils.getSetterName(generateFieldName());
	}

	@Override
	protected Class<?> getAccessMethodType() {
		return getFieldType();
	}

	@Override
	public boolean isAggregator() {
		return false;
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		super.generate(clazz);
		generateSetter(clazz);
	}

	protected void generateSetter(final CtClass clazz) throws Exception {
		final CtMethod method = CtMethod.make(TemplateHelper.setter(
				getModifier(), generateFieldName(), getFieldType()), clazz);

		clazz.addMethod(method);
	}

}
