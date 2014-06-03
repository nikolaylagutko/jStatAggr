package org.gerzog.jstataggr.core.manager.impl.internal;

import java.lang.invoke.MethodHandle;

import javassist.CtClass;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IStatisticsField {

	void generate(CtClass clazz) throws Exception;

	MethodHandle getAccessMethod(Class<?> clazz) throws Exception;

}
