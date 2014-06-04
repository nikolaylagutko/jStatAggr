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
package org.gerzog.jstataggr.core.collector.impl;

import java.util.HashMap;
import java.util.Map;

import org.gerzog.jstataggr.IStatisticsKey;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsKey implements IStatisticsKey {

	public static class StatisticsKeyBuilder {

		private final StatisticsKey result = new StatisticsKey();

		public StatisticsKeyBuilder withParameter(final String keyName,
				final Object keyValue) {
			result.keyParts.put(keyName, keyValue);

			return this;
		}

		public IStatisticsKey build() {
			result.generateHashCode();

			return result;
		}

	}

	private final Map<String, Object> keyParts = new HashMap<>();

	private int hashCode;

	private StatisticsKey() {

	}

	private void generateHashCode() {
		hashCode = keyParts.hashCode();
	}

	@Override
	public Object get(final String name) {
		return keyParts.get(name);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof StatisticsKey) {
			return ((StatisticsKey) o).keyParts.equals(keyParts);
		}

		return false;
	}

	@Override
	public String toString() {
		return keyParts.toString();
	}

}
