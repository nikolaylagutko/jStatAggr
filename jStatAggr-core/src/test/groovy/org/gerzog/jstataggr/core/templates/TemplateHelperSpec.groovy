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
		def result = TemplateHelper.getter('public', "value", Integer.class)

		then:
		result == 'public java.lang.Integer getValue() {return this.value;}'
	}

	def "check method creation"() {
		when:
		def result = TemplateHelper.method('public', 'value', 'java.lang.Integer', '', 'java.lang.String', 'param1', 'spock.lang.Specification', 'param2')

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
		def result = TemplateHelper.setter('public', 'value', Integer.class)

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
	def "check simple updater body"(AggregationType aggregation, String postfix) {
		when:
		def result = TemplateHelper.simpleUpdaterBody('value', aggregation)

		then:
		result == "this.value${postfix} = org.gerzog.jstataggr.core.functions.FunctionHelper.apply(org.gerzog.jstataggr.AggregationType.${aggregation.name()}, value, this.value${postfix});"

		where:
		aggregation 		| postfix
		AggregationType.MIN | 'Min'
		AggregationType.MAX | 'Max'
		AggregationType.SUM	| 'Sum'
	}

	@Unroll
	def "check unsupported simple updaters"(AggregationType aggregation) {
		when:
		def result = TemplateHelper.simpleUpdaterBody('value', aggregation)

		then:
		thrown(IllegalArgumentException)

		where:
		aggregation << [
			AggregationType.AVERAGE
		]
	}

	@Unroll
	def "check updater method"(AggregationType aggregation, String postfix) {
		when:
		def result = TemplateHelper.simpleUpdater('public', 'value', int.class, aggregation)

		then:
		result.startsWith("public void updateValue${postfix}(int value)")

		where:
		aggregation			| postfix
		AggregationType.MIN | 'Min'
		AggregationType.MAX | 'Max'
		AggregationType.SUM | 'Sum'
	}

	@Unroll
	def "check name of type is correct"(def type, def expected) {
		when:
		def result = TemplateHelper.getTypeName(type)

		then:
		result == expected

		where:
		type 			| expected
		String.class 	| 'java.lang.String'
		int.class 		| 'int'
		List.class 		| 'java.util.List'
		int[].class 	| 'int[]'
		String[].class	| 'java.lang.String[]'
		null			| null
	}

	def "check body for average getter"() {
		when:
		def result = TemplateHelper.averageGetterBody('value')

		then:
		result == 'return this.valueSum / this.valueCount;'
	}

	def "check method call generator"() {
		when:
		def result = TemplateHelper.methodCall('method', 'param1', 'param2')

		then:
		result == 'method(param1, param2);'
	}

	def "check average updater method"() {
		when:
		def result = TemplateHelper.averageUpdaterBody('averageField')

		then:
		result == 'this.averageField = org.gerzog.jstataggr.core.functions.FunctionHelper.apply(averageField, averageFieldCount, this.averageField);'
	}
}
