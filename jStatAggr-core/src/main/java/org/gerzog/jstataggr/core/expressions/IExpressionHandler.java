package org.gerzog.jstataggr.core.expressions;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IExpressionHandler {

	Object invokeExpression(String expressions, Object originalValue)
			throws Exception;

}
