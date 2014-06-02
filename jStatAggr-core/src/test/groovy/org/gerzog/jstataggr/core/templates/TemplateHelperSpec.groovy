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
package org.gerzog.jstataggr.core.templates

import org.gerzog.jstataggr.AggregationType

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class TemplateHelperSpec extends Specification {

	def "check getter method"() {
		when:
		def result = TemplateHelper.getter("value", Integer.class)

		then:
		result == 'public java.lang.Integer getValue() {return this.value;}'
	}

	def "check method creation"() {
		when:
		def result = TemplateHelper.method('value', Integer.class, '', String, 'param1', Specification, 'param2')

		then:
		result == 'public java.lang.Integer value(java.lang.String param1, spock.lang.Specification param2) {}'
	}

	def "check getter method body"() {
		when:
		def result = TemplateHelper.getterBody('value')

		then:
		result == 'return this.value;'
	}

	def "check setter method"() {
		when:
		def result = TemplateHelper.setter('value', Integer.class)

		then:
		result == 'public void setValue(java.lang.Integer value) {this.value = value;}'
	}

	def "check setter method body" () {
		when:
		def result = TemplateHelper.setterBody('value')

		then:
		result == 'this.value = value;'
	}

	@Unroll
	def "check simple updater body"(AggregationType aggregation) {
		when:
		def result = TemplateHelper.simpleUpdaterBody('value', aggregation)

		then:
		result == "org.gerzog.jstataggr.core.functions.FunctionHelper.apply(org.gerzog.jstataggr.AggregationType.${aggregation.name()}, this.value, value);"

		where:
		aggregation << [
			AggregationType.MIN,
			AggregationType.MAX,
			AggregationType.SUM
		]
	}

	@Unroll
	def "check unsupported simple updaters"(AggregationType aggregation) {
		when:
		def result = TemplateHelper.simpleUpdaterBody('value', aggregation)

		then:
		thrown(IllegalArgumentException)

		where:
		aggregation << [
			AggregationType.AVERAGE,
			AggregationType.COUNT
		]
	}

	@Unroll
	def "check updater method"(AggregationType aggregation, String postfix) {
		when:
		def result = TemplateHelper.simpleUpdater('value', int.class, aggregation)

		then:
		result.startsWith("public void updateValue${postfix}(int value)")

		where:
		aggregation			| postfix
		AggregationType.MIN | 'Min'
		AggregationType.MAX | 'Max'
		AggregationType.SUM | 'Sum'
	}
}
