package org.gerzog.jstataggr.core.expressions;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IExpressionHandler {

	<T> T invokeExpression(String expressions, T originalValue) throws Exception;

}
