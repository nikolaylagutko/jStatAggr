package org.gerzog.jstataggr.core.manager.impl

import org.gerzog.jstataggr.core.manager.impl.StatisticsKey.StatisticsKeyBuilder

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class StatisticsKeySpec extends Specification {

	def "check equals"() {
		when:
		def key1 = createKey(['name1': 'value1', 'name2': 'value2'])
		def key2 = createKey(['name1': 'value1', 'name2': 'value2'])

		then:
		key1.equals(key2)
	}

	def "check not equals"() {
		when:
		def key1 = createKey(['name1': 'value1', 'name2': 'value2'])
		def key2 = createKey(['name1': 'value1', 'name2': 'value3'])

		then:
		!key1.equals(key2)
	}

	def "check hash equals"() {
		when:
		def key1 = createKey(['name1': 'value1', 'name2': 'value2'])
		def key2 = createKey(['name1': 'value1', 'name2': 'value2'])

		then:
		key1.hashCode() == key2.hashCode()
	}

	def "check hash not equals"() {
		when:
		def key1 = createKey(['name1': 'value1', 'name2': 'value2'])
		def key2 = createKey(['name1': 'value1', 'name2': 'value3'])

		then:
		key1.hashCode() != key2.hashCode()
	}

	def createKey(def propertiesMap) {
		StatisticsKeyBuilder builder = new StatisticsKeyBuilder()

		propertiesMap.each { key, value ->
			builder.withParameter(key, value)
		}

		builder.build()
	}
}
