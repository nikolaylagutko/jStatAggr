package org.gerzog.jstataggr.core.internal;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IStatisticsManager {

	void updateStatistics(Object statisticsEntry, Class<?> statisticsClass, String statisticsName);

}
