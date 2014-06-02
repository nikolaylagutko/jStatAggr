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
