package org.gerzog.jstataggr.core.impl;

import org.gerzog.jstataggr.core.IStatisticsManager;
import org.gerzog.jstataggr.core.internal.impl.AbstractStatisticsHandler;

/**
 * Statistics Handler that runs in Synchronized Mode
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class SynchronizedStatisticsHandler extends AbstractStatisticsHandler {

	public SynchronizedStatisticsHandler(final IStatisticsManager statistcsManager) {
		super(statistcsManager);
	}

	public SynchronizedStatisticsHandler() {
		super();
	}

	@Override
	protected void handleStatistics(final Runnable action) {
		action.run();
	}

}
