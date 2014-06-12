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

	private final Class<?> fieldType;

	private final Class<?> methodType;

	private final String modifier;

	protected AbstractStatisticsField(final String modifier,
			final String originalFieldName, final Class<?> methodType,
			final Class<?> fieldType) {
		this.originalFieldName = originalFieldName;
		this.fieldType = fieldType;
		this.modifier = modifier;
		this.methodType = methodType;
	}

	protected AbstractStatisticsField(final String originalFieldName,
			final Class<?> methodType, final Class<?> fieldType) {
		this("public", originalFieldName, methodType, fieldType);
	}

	protected AbstractStatisticsField(final String originalFieldName,
			final Class<?> fieldType) {
		this("public", originalFieldName, fieldType, fieldType);
	}

	protected AbstractStatisticsField(final String modifier,
			final String originalFieldName, final Class<?> dataType) {
		this(modifier, originalFieldName, dataType, dataType);
	}

	@Override
	public void generate(final CtClass clazz) throws Exception {
		generateField(clazz);
		generateGetter(clazz);
	}

	protected void generateField(final CtClass clazz) throws Exception {
		final CtClass dataClazz = CLASS_POOL.get(fieldType.getName());

		final CtField field = new CtField(dataClazz, generateFieldName(), clazz);

		addField(clazz, field);
	}

	protected void generateGetter(final CtClass clazz) throws Exception {
		final CtMethod method = CtMethod.make(TemplateHelper.getter(modifier,
				generateFieldName(), getGetterCastType(), fieldType), clazz);

		clazz.addMethod(method);
	}

	protected Class<?> getGetterCastType() {
		return methodType;
	}

	protected void addField(final CtClass clazz, final CtField field)
			throws Exception {
		clazz.addField(field);
	}

	protected String generateFieldName() {
		return originalFieldName;
	}

	protected Class<?> getFieldType() {
		return fieldType;
	}

	@Override
	public Class<?> getMethodType() {
		return methodType;
	}

	@Override
	public MethodHandle getAccessMethodHandle(final Class<?> clazz)
			throws Exception {
		final Method method = clazz.getMethod(getAccessMethodName(),
				getAccessMethodType());

		return MethodHandles.lookup().unreflect(method);
	}

	protected abstract String getAccessMethodName();

	protected abstract Class<?> getAccessMethodType();

	@Override
	public String getFieldName() {
		return originalFieldName;
	}

	protected String getModifier() {
		return modifier;
	}
}
