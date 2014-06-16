/*
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
package org.gerzog.jstataggr.sample.csv.internal

import org.gerzog.jstataggr.AggregationType
import org.gerzog.jstataggr.annotations.Aggregated
import org.gerzog.jstataggr.annotations.Expression
import org.gerzog.jstataggr.annotations.StatisticsEntry
import org.gerzog.jstataggr.annotations.StatisticsKey

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@StatisticsEntry
class Statistics {

	@StatisticsKey
	@Expression('#this / @properties.timestampFactor')
	long timestamp

	@StatisticsKey
	String key

	@Aggregated(AggregationType.SUM)
	long value
}
