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
import java.util.Map;

/**
 * Main entry point to work with statistics collectors
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IStatisticsManager {

	/**
	 * Updated statistics collector data
	 *
	 * @param statisticsEntry
	 *            - new piece of data to update statistics
	 * @param statisticsName
	 *            - name of statistics
	 */
	void updateStatistics(Object statisticsEntry, String statisticsName);

	/**
	 * Collects corresponding statistics data
	 * 
	 * @param statisticsName
	 *            - name of statistics to collect
	 * @param filter
	 *            - additional filtering that should be applied for statistics data
	 * @param cleanup
	 *            - should collected statistics be removed
	 */
	Map<String, Collection<Object>> collectStatistics(String statisticsName, IStatisticsFilter filter, boolean cleanup);

}
