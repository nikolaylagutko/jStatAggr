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

import org.apache.commons.lang3.time.DateFormatUtils
import org.gerzog.jstataggr.core.impl.SynchronizedStatisticsHandler
import org.gerzog.jstataggr.core.manager.impl.StatisticsManagerImpl
import org.gerzog.jstataggr.expressions.spel.SpelExpressionHandler
import org.gerzog.jstataggr.writers.csv.CSVStatisticsWriter
import org.gerzog.jstataggr.writers.csv.ICSVWriterConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@Configuration
class CSVandSPELStatisticsConfig {

	@Bean
	public properties() {
		return new Properties()
	}

	@Bean
	public statisticsConfig() {
		ICSVWriterConfig config = new ICSVWriterConfig() {
					String getFilename(String statisticsName, Object statisticsData) {
						def timestamp = statisticsData.timestamp

						"${System.properties['java.io.tmpdir']}${File.separator}${statisticsName} ${DateFormatUtils.format(timestamp * 1000, 'yyyy-MM-dd HH:mm:ss')}"
					}

					List<String> getExcludedFields() {
						['timestamp']
					}

					boolean isApplied(String statisticsName) {
						statisticsName == 'Statistics'
					}

					Comparator<String> getFieldComparator() {
						null
					}
				}
	}

	@Bean
	public statisticsManager() {
		new StatisticsManagerImpl(expressionHandler())
	}

	@Bean
	public expressionHandler() {
		new SpelExpressionHandler()
	}

	@Bean
	public statisticsWriter() {
		new CSVStatisticsWriter([statisticsConfig()])
	}

	@Bean
	public statisticsHandler() {
		def result = new SynchronizedStatisticsHandler(statisticsManager())

		result.statisticsWriters = [statisticsWriter()]

		result
	}
}
