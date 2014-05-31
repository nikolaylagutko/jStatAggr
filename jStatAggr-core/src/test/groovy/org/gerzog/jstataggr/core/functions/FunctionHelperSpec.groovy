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
package org.gerzog.jstataggr.core.functions

import org.gerzog.jstataggr.core.AggregationType

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class FunctionHelperSpec extends Specification {

	def "check AVERAGE function for int apply()"() {
		when:
		FunctionHelper.apply(AggregationType.AVERAGE, 0, 0)

		then:
		thrown(IllegalStateException)
	}

	def "check COUNT function for int apply()"() {
		when:
		FunctionHelper.apply(AggregationType.COUNT, 0, 0)

		then:
		thrown(IllegalStateException)
	}

	@Unroll
	def "check apply for int"(AggregationType type, int current, int update, int result) {
		when:
		def output = FunctionHelper.apply(type, current, update)

		then:
		output == result

		where:
		type 				| current | update | result
		AggregationType.MAX	| 10	  | 20	   | 20
		AggregationType.MAX	| 20	  | 10	   | 20
		AggregationType.MIN	| 10	  | 20	   | 10
		AggregationType.MIN	| 20	  | 10	   | 10
		AggregationType.SUM	| 10	  | 20	   | 30
	}

	def "check AVERAGE function for long apply()"() {
		when:
		FunctionHelper.apply(AggregationType.AVERAGE, 0l, 0l)

		then:
		thrown(IllegalStateException)
	}

	def "check COUNT function for long apply()"() {
		when:
		FunctionHelper.apply(AggregationType.COUNT, 0l, 0l)

		then:
		thrown(IllegalStateException)
	}

	@Unroll
	def "check apply for long"(AggregationType type, long current, long update, long result) {
		when:
		def output = FunctionHelper.apply(type, current, update)

		then:
		output == result

		where:
		type 				| current | update | result
		AggregationType.MAX	| 10	  | 20	   | 20
		AggregationType.MAX	| 20	  | 10	   | 20
		AggregationType.MIN	| 10	  | 20	   | 10
		AggregationType.MIN	| 20	  | 10	   | 10
		AggregationType.SUM	| 10	  | 20	   | 30
	}
}
