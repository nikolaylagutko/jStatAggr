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
package org.gerzog.jstataggr.writers.csv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.ComparatorUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface ICSVWriterConfig {

	String getFilename(String statisticsName, Object statisticsData);

	default List<String> getExcludedFields() {
		return new ArrayList<>(0);
	}

	boolean isApplied(String statisticsName);

	default Comparator<String> getFieldComparator() {
		return ComparatorUtils.naturalComparator();
	}

}
