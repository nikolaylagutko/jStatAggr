/**
 * Copyright (C)2014 - Nikolay Lagutko <nikolay.lagutko@mail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gerzog.jstataggr;

import java.util.Collection;

/**
 * Main entry point to handle Statistics. Updates aggregated statistics data for
 * each annotated class.
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 */
public interface IStatisticsHandler {

	/**
	 * Updates aggregated data based on entry class annotations
	 */
	void handleStatistics(Object statisticsEntry);

	void writeStatistics(boolean cleanup) throws Exception;

	void writeStatistics(String statisticsName, boolean cleanup)
			throws Exception;

	void writeStatistics(String statisticsName, IStatisticsFilter filter,
			boolean cleanup) throws Exception;

	void setStatisticsWriters(Collection<IStatisticsWriter> writers)
			throws Exception;

}
