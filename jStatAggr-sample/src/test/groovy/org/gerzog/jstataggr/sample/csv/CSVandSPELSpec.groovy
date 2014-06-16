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
package org.gerzog.jstataggr.sample.csv

import org.apache.commons.lang3.time.DateFormatUtils
import org.gerzog.jstataggr.IStatisticsHandler
import org.gerzog.jstataggr.sample.csv.internal.CSVandSPELStatisticsConfig
import org.gerzog.jstataggr.sample.csv.internal.Statistics
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.supercsv.io.CsvBeanReader
import org.supercsv.prefs.CsvPreference

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@ContextConfiguration(classes = CSVandSPELStatisticsConfig.class)
class CSVandSPELSpec extends Specification {

	@Autowired
	IStatisticsHandler handler

	def "check csv statistics export"() {
		setup:
		def timestamps = [] as Set
		def counts = [] as Map

		when:
		100.times {
			Statistics statistics = new Statistics()

			statistics.timestamp = System.currentTimeMillis()
			statistics.key = it % 10
			statistics.value = it

			timestamps << (long)statistics.timestamp / 1000

			handler.handleStatistics(statistics)

			Thread.sleep(100)
		}

		and:
		handler.writeStatistics(true)

		then:
		noExceptionThrown()

		timestamps.each {
			long timestamp = it * 1000
			def file = new File("${System.properties['java.io.tmpdir']}${File.separator}Statistics ${DateFormatUtils.format(timestamp, 'yyyy-MM-dd HH:mm:ss')}.csv")

			assert file.exists()

			file.withReader {
				def reader = new CsvBeanReader(it, CsvPreference.STANDARD_PREFERENCE)

				def headers = reader.getHeader(true)

				assert headers.length == 2
				assert headers[0] == 'key'
				assert headers[1] == 'valueSum'
			}
		}
	}
}
