package org.gerzog.jstataggr;

import java.util.Collection;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IStatisticsWriter {

	void writeStatistics(String statisticsName, Collection<Object> statisticsData);

}
