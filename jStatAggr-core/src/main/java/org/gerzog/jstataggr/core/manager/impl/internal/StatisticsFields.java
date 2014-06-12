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
package org.gerzog.jstataggr.core.manager.impl.internal;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.FieldType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class StatisticsFields {

	private StatisticsFields() {

	}

	public static IStatisticsField forStatisticsKey(final String name, final Class<?> dataType) {
		return new StatisticsField(name, dataType);
	}

	public static IStatisticsField forAggregation(final String name, final Class<?> dataType, final AggregationType aggregationType, final FieldType fieldType) {
		switch (aggregationType) {
		case MIN:
		case MAX:
		case SUM:
			return new AggregationStatisticsField(name, dataType, aggregationType, fieldType);
		case COUNT:
			return new CountAggregationStatisticsField(name, dataType, aggregationType, fieldType);
		case AVERAGE:
			return new AverageAggregationStatisticsField(name, dataType, aggregationType, fieldType);
		default:
			throw new IllegalStateException("Unsupported enum <" + aggregationType + ">");
		}
	}

}
