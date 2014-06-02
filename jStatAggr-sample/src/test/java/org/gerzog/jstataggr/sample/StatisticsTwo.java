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
public class StatisticsTwo {

	@Aggregated({ AggregationType.MIN })
	private int value1;

	@Aggregated({ AggregationType.MAX })
	private int value2;

	@Aggregated({ AggregationType.SUM })
	private int value3;

	@StatisticsKey
	private String key;

	@StatisticsKey
	private int anotherKey;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public int getAnotherKey() {
		return anotherKey;
	}

	public void setAnotherKey(final int anotherKey) {
		this.anotherKey = anotherKey;
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(final int value1) {
		this.value1 = value1;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(final int value2) {
		this.value2 = value2;
	}

	public int getValue3() {
		return value3;
	}

	public void setValue3(final int value3) {
		this.value3 = value3;
	}

}
