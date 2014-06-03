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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.gerzog.jstataggr.core.templates.TemplateHelper;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
abstract class AbstractStatisticsField implements IStatisticsField {

	protected static final ClassPool CLASS_POOL = ClassPool.getDefault();

	private final String originalFieldName;

	private final Class<?> dataType;

	protected AbstractStatisticsField(final String originalFieldName, final Class<?> dataType) {
		this.originalFieldName = originalFieldName;
		this.dataType = dataType;
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		generateField(clazz);
		generateGetter(clazz);
		generateSetter(clazz);
	}

	protected void generateField(final CtClass clazz) throws Exception {
		final CtClass dataClazz = CLASS_POOL.get(dataType.getName());

		final CtField field = new CtField(dataClazz, generateFieldName(), clazz);

		addField(clazz, field);
	}

	protected void generateGetter(final CtClass clazz) throws Exception {
		final CtMethod method = CtMethod.make(TemplateHelper.getter(generateFieldName(), dataType), clazz);

		clazz.addMethod(method);
	}

	protected void addField(final CtClass clazz, final CtField field) throws Exception {
		clazz.addField(field);
	}

	protected void generateSetter(final CtClass clazz) throws Exception {
		final CtMethod method = CtMethod.make(TemplateHelper.setter(generateFieldName(), dataType), clazz);

		clazz.addMethod(method);
	}

	protected String generateFieldName() {
		return originalFieldName;
	}

	protected Class<?> getDataType() {
		return dataType;
	}

	@Override
	public MethodHandle getAccessMethod(final Class<?> clazz) throws Exception {
		final Method method = clazz.getMethod(getAccessMethodName(), getAccessMethodType());

		return MethodHandles.lookup().unreflect(method);
	}

	protected abstract String getAccessMethodName();

	protected abstract Class<?> getAccessMethodType();
}
