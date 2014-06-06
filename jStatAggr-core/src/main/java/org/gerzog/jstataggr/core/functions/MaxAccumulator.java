package org.gerzog.jstataggr.core.functions;

import java.util.concurrent.atomic.LongAccumulator;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class MaxAccumulator extends LongAccumulator {

	private static final long serialVersionUID = 6795330008386934057L;

	public MaxAccumulator() {
		super((left, right) -> Math.max(left, right), Long.MIN_VALUE);
	}

}
