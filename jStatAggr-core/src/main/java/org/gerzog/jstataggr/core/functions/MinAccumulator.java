package org.gerzog.jstataggr.core.functions;

import java.util.concurrent.atomic.LongAccumulator;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class MinAccumulator extends LongAccumulator {

	private static final long serialVersionUID = 6198909126811980974L;

	public MinAccumulator() {
		super((left, right) -> Math.min(left, right), Long.MAX_VALUE);
	}

}
