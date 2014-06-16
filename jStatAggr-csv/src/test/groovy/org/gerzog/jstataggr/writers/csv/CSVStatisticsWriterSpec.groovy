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

import org.apache.commons.collections4.ComparatorUtils
import org.gerzog.jstataggr.writers.csv.CSVStatisticsWriter.WritersCache
import org.supercsv.io.ICsvBeanWriter

import spock.lang.Specification


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class CSVStatisticsWriterSpec extends Specification {

	private class SimpleBean {

		int intValue

		String stringValue

		long longValue
	}

	static final STATISTICS_NAME = 'statistics'

	static final FILE_NAME = 'filename'

	CSVStatisticsWriter writer = Spy(CSVStatisticsWriter)

	ICSVWriterConfig appliedConfig = Mock(ICSVWriterConfig)
	ICSVWriterConfig anotherConfig = Mock(ICSVWriterConfig)

	def setup() {
		appliedConfig.isApplied(STATISTICS_NAME) >> true
		anotherConfig.isApplied(STATISTICS_NAME) >> false
	}

	//	@Unroll
	//	def "check an exception occured when no config found"(def configs, def continues) {
	//		setup:
	//		writer.configurations = configs
	//
	//		when:
	//		writer.writeStatistics(STATISTICS_NAME, [])
	//
	//		then:
	//		continues * writer.writeStatistics(_ as List<ICSVWriterConfig>, STATISTICS_NAME, [], _ as WritersCache)
	//
	//		where:
	//		configs 							| continues
	//		[]									| 0
	//		[nonAppliedConfig]					| 0
	//		[appliedConfig]						| 1
	//		[nonAppliedConfig, appliedConfig]   | 1
	//		[appliedConfig, appliedConfig]      | 1
	//	}

	def "check main workflow of writeStatistics"() {
		setup:
		writer.configurations = [appliedConfig]

		when:
		writer.writeStatistics(STATISTICS_NAME, [])

		then:
		1 * writer.writeStatistics(_ as List<ICSVWriterConfig>, STATISTICS_NAME, [], _ as WritersCache)
		1 * writer.finishUp(_ as WritersCache)
	}

	def "check all writers closed on finishup"() {
		setup:
		WritersCache cache = new WritersCache()
		def writers = []
		10.times {
			ICsvBeanWriter writer = Mock(ICsvBeanWriter)
			writers << writer
			cache.registerWriter(STATISTICS_NAME, "file${it}", writer)
		}
		writer.configurations = [appliedConfig]

		when:
		writer.finishUp(cache)

		then:
		writers.each {
			1 * it.close()
		}
	}

	def "check writer preparations"() {
		setup:
		def object = new Object()
		def WritersCache cache = Spy(WritersCache)
		def ICsvBeanWriter csvWriter = Mock(ICsvBeanWriter)
		def columnsNames = [] as String[]

		when:
		writer.writeStatistics([
			appliedConfig,
			anotherConfig
		], STATISTICS_NAME, [object, object], cache)

		then:
		2 * writer.prepareWriter(appliedConfig, STATISTICS_NAME, object, cache) >> csvWriter
		2 * writer.prepareWriter(anotherConfig, STATISTICS_NAME, object, cache) >> csvWriter

		2 * writer.defineColumnNames(appliedConfig, object, csvWriter, cache) >> columnsNames
		2 * writer.defineColumnNames(anotherConfig, object, csvWriter, cache) >> columnsNames

		4 * csvWriter.write(object, columnsNames)
	}

	def "check prepare writer for a new writer"() {
		setup:
		def object = new Object()
		def cache = Mock(WritersCache)
		def csvWriter = Mock(ICsvBeanWriter)
		def columnsNames = [] as String[]

		when:
		def result = writer.prepareWriter(appliedConfig, STATISTICS_NAME, object, cache)

		then:
		1 * appliedConfig.getFilename(STATISTICS_NAME, object) >> FILE_NAME
		1 * cache.getWriter(STATISTICS_NAME, FILE_NAME) >> null
		1 * writer.createWriter(FILE_NAME) >> csvWriter
		1 * cache.registerWriter(STATISTICS_NAME, FILE_NAME, csvWriter)
		1 * writer.defineColumnNames(appliedConfig, object, csvWriter, cache) >> columnsNames
		1 * csvWriter.writeHeader(columnsNames)

		result == csvWriter
	}

	def "check prepare writer for existing writer"() {
		setup:
		def object = new Object()
		def cache = Mock(WritersCache)
		def csvWriter = Mock(ICsvBeanWriter)
		def columnsNames = [] as String[]

		when:
		def result = writer.prepareWriter(appliedConfig, STATISTICS_NAME, object, cache)

		then:
		1 * appliedConfig.getFilename(STATISTICS_NAME, object) >> FILE_NAME
		1 * cache.getWriter(STATISTICS_NAME, FILE_NAME) >> csvWriter
		0 * writer.createWriter(FILE_NAME) >> csvWriter
		0 * cache.registerWriter(STATISTICS_NAME, FILE_NAME, csvWriter)
		0 * writer.defineColumnNames(appliedConfig, object, csvWriter, cache) >> columnsNames
		0 * csvWriter.writeHeader(columnsNames)

		result == csvWriter
	}

	def "check create writer on unexisting file"() {
		setup:
		def filename = "${System.properties['java.io.tmpdir']}${File.separator}filename"

		when:
		ICsvBeanWriter result = writer.createWriter(filename)

		then:
		noExceptionThrown()

		result != null
		new File(filename + ".csv").exists()

		cleanup:
		new File(filename + ".csv").delete()
	}

	def "check create writer on existing file"() {
		setup:
		def filename = "${System.properties['java.io.tmpdir']}${File.separator}filename"
		new File(filename + ".csv").createNewFile()

		when:
		ICsvBeanWriter result = writer.createWriter(filename)

		then:
		noExceptionThrown()

		result != null
		new File(filename + "-1.csv").exists()

		cleanup:
		new File(filename + ".csv").delete()
		new File(filename + "-1.csv").delete()
	}

	def "define column names that exists in cache"() {
		setup:
		def columnNames = [] as String[]
		ICsvBeanWriter csvWriter = Mock(ICsvBeanWriter)
		WritersCache cache = Mock(WritersCache)

		when:
		def result = writer.defineColumnNames(appliedConfig, new Object(), csvWriter, cache)

		then:
		1 * cache.getColumns(csvWriter) >> columnNames
		result == columnNames
	}

	def "define column names without any cahced info"() {
		setup:
		def columnNames = ['field']
		WritersCache cache = Mock(WritersCache)
		ICsvBeanWriter csvWriter = Mock(ICsvBeanWriter)
		def object = new SimpleBean()

		when:
		def result = writer.defineColumnNames(appliedConfig, object, csvWriter, cache)

		then:
		1 * cache.getColumns(csvWriter) >> null
		1 * writer.getFieldNames(SimpleBean, appliedConfig) >> columnNames
		1 * cache.registerColumns(csvWriter, columnNames)
	}

	def "define column names with existing bean info"() {
		setup:
		def columnNames = ['field']
		WritersCache cache = Mock(WritersCache)
		ICsvBeanWriter csvWriter = Mock(ICsvBeanWriter)

		when:
		def result = writer.defineColumnNames(appliedConfig, new SimpleBean(), csvWriter, cache)

		then:
		1 * cache.getColumns(csvWriter) >> null
		1 * writer.getFieldNames(SimpleBean, appliedConfig) >> columnNames
		1 * cache.registerColumns(csvWriter, columnNames)
	}

	def "check field names"() {
		setup:
		appliedConfig.getExcludedFields() >> []
		appliedConfig.getFieldComparator() >> ComparatorUtils.naturalComparator()

		when:
		def result = writer.getFieldNames(SimpleBean, appliedConfig)

		then:
		result != null
		result == [
			'intValue',
			'longValue',
			'stringValue'
		]
	}

	def "check field names with excluded field"() {
		setup:
		appliedConfig.getExcludedFields() >> ['longValue']
		appliedConfig.getFieldComparator() >> ComparatorUtils.naturalComparator()

		when:
		def result = writer.getFieldNames(SimpleBean, appliedConfig)

		then:
		result != null
		result == ['intValue', 'stringValue']
	}

	def "check field names with comparator"() {
		setup:
		appliedConfig.getExcludedFields() >> []
		appliedConfig.getFieldComparator() >> lengthComparator()

		when:
		def result = writer.getFieldNames(SimpleBean, appliedConfig)

		then:
		result != null
		result == [
			'stringValue',
			'longValue',
			'intValue'
		]
	}

	def lengthComparator() {
		new Comparator<String>() {
					public int compare(String first, String second) {
						return second.length() - first.length()
					}
				}
	}
}
