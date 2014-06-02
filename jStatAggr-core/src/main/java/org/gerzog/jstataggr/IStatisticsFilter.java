package org.gerzog.jstataggr;

import org.gerzog.jstataggr.core.manager.impl.StatisticsKey;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@FunctionalInterface
public interface IStatisticsFilter {

	boolean isApplied(StatisticsKey statisticsKey);

}
