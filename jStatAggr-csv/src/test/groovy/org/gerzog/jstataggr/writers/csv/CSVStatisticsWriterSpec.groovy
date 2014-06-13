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
package org.gerzog.jstataggr.writers.csv

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class CSVStatisticsWriterSpec extends Specification {

	static final STATISTICS_NAME = 'statistics'

	CSVStatisticsWriter writer = Spy(CSVStatisticsWriter)

	@Shared
	ICSVWriterConfig appliedConfig = Mock(ICSVWriterConfig, {
		it.isApplied(STATISTICS_NAME) >> true
	})
	@Shared
	ICSVWriterConfig nonAppliedConfig = Mock(ICSVWriterConfig)

	def setup() {
		appliedConfig.isApplied(STATISTICS_NAME) >> true
		nonAppliedConfig.isApplied(STATISTICS_NAME) >> false
	}

	@Unroll
	def "check an exception occured when no config found"(def configs, def continues) {
		setup:
		writer.configurations = configs
		
		when:
		writer.writeStatistics(STATISTICS_NAME, [])

		then:
		continues * writer.writeStatistics(_ as List<ICSVWriterConfig>, STATISTICS_NAME, [])

		where:
		configs 							| continues
		[]									| 0
		[nonAppliedConfig] 					| 0
		[appliedConfig]						| 1
		[nonAppliedConfig, appliedConfig]	| 1
		[appliedConfig, appliedConfig]		| 1
	}
}
