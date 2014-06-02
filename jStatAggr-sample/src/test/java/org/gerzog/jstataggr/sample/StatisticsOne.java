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
package org.gerzog.jstataggr.sample;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.annotations.Aggregated;
import org.gerzog.jstataggr.annotations.StatisticsEntry;
import org.gerzog.jstataggr.annotations.StatisticsKey;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@StatisticsEntry("sample_statistics_one")
public class StatisticsOne {

	@Aggregated({ AggregationType.MIN, AggregationType.MAX,
			AggregationType.SUM, AggregationType.AVERAGE })
	private int value;

	@StatisticsKey
	private String key;

	@StatisticsKey
	private String anotherKey;

	public int getValue() {
		return value;
	}

	public void setValue(final int value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getAnotherKey() {
		return anotherKey;
	}

	public void setAnotherKey(final String anotherKey) {
		this.anotherKey = anotherKey;
	}

}
