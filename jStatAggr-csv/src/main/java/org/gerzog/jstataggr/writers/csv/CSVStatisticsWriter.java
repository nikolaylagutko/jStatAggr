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

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.gerzog.jstataggr.IStatisticsWriter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class CSVStatisticsWriter implements IStatisticsWriter {

	private List<ICSVWriterConfig> configurations;

	public CSVStatisticsWriter() {
		this(new ArrayList<>(0));
	}

	public CSVStatisticsWriter(final List<ICSVWriterConfig> configurations) {
		this.configurations = configurations;
	}

	public void setConfigurations(final List<ICSVWriterConfig> configurations) {
		notNull(configurations, "Configurations cannot be null");

		this.configurations = configurations;
	}

	@Override
	public void writeStatistics(final String statisticsName,
			final Collection<Object> statisticsData) {
		final List<ICSVWriterConfig> appliedConfigs = defineConfigs(statisticsName);

		if (!appliedConfigs.isEmpty()) {
			writeStatistics(appliedConfigs, statisticsName, statisticsData);
		}
	}

	protected List<ICSVWriterConfig> defineConfigs(final String statisticsName) {
		return configurations.stream()
				.filter(config -> config.isApplied(statisticsName))
				.collect(Collectors.toList());
	}

	protected void writeStatistics(final List<ICSVWriterConfig> config,
			final String statisticsName, final Collection<Object> statisticsData) {

	}

}
