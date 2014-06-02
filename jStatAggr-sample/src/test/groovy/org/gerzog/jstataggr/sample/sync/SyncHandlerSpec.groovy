package org.gerzog.jstataggr.sample.sync

import org.gerzog.jstataggr.IStatisticsHandler
import org.gerzog.jstataggr.IStatisticsManager
import org.gerzog.jstataggr.core.impl.SynchronizedStatisticsHandler
import org.gerzog.jstataggr.sample.AbstractSampleSpec

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class SyncHandlerSpec extends AbstractSampleSpec {

	@Override
	public IStatisticsHandler createHandler(IStatisticsManager manager) {
		new SynchronizedStatisticsHandler(manager)
	}
}
