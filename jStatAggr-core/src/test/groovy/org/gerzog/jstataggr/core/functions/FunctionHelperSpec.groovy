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

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

import org.gerzog.jstataggr.AggregationType

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

	@Unroll
	def "check apply for int"(AggregationType type, int current, int update, int result) {
		when:
		def output = FunctionHelper.apply(type, update, current)

		then:
		output == result

		where:
		type 					| current | update | result
		AggregationType.MAX		| 10	  | 20	   | 20
		AggregationType.MAX		| 20	  | 10	   | 20
		AggregationType.MIN		| 10	  | 20	   | 10
		AggregationType.MIN		| 20	  | 10	   | 10
		AggregationType.SUM		| 10	  | 20	   | 30
		AggregationType.COUNT 	| 10	  | 20	   | 11
	}

	def "check AVERAGE function for long apply()"() {
		when:
		FunctionHelper.apply(AggregationType.AVERAGE, 0l, 0l)

		then:
		thrown(IllegalStateException)
	}

	@Unroll
	def "check apply for long"(AggregationType type, long current, long update, long result) {
		when:
		def output = FunctionHelper.apply(type, update, current)

		then:
		output == result

		where:
		type 					| current | update | result
		AggregationType.MAX		| 10	  | 20	   | 20
		AggregationType.MAX		| 20	  | 10	   | 20
		AggregationType.MIN		| 10	  | 20	   | 10
		AggregationType.MIN		| 20	  | 10	   | 10
		AggregationType.SUM		| 10	  | 20	   | 30
		AggregationType.COUNT 	| 10	  | 20	   | 11
	}

	@Unroll
	def "check count for object with int result"(Object update, int expectedResult) {
		when:
		def output = FunctionHelper.apply(AggregationType.COUNT, update, 0)

		then:
		output == expectedResult

		where:
		update 		 		| expectedResult
		new Object() 		| 1
		[1, 2, 3].toArray() | 3
		[1: 1, 2: 2, 3: 3]  | 3
		[1, 2, 3] as List 	| 3
	}

	@Unroll
	def "check apply for object with non-count aggregation"(def aggregationType, def current, def update) {
		when:
		FunctionHelper.apply(aggregationType, update, current)

		then:
		thrown(IllegalStateException)

		where:
		aggregationType 		| current			| update
		AggregationType.AVERAGE | 10l				| 10
		AggregationType.MAX		| 10l				| 10
		AggregationType.MIN		| 10l				| 10
		AggregationType.SUM		| 10l				| 10
		AggregationType.AVERAGE | 10l				| new Object()
		AggregationType.MAX		| 10l				| new Object()
		AggregationType.MIN		| 10l				| new Object()
		AggregationType.SUM		| 10l				| new Object()
	}

	@Unroll
	def "check count for object with long result"(Object update, int expectedResult) {
		when:
		def output = FunctionHelper.apply(AggregationType.COUNT, update, 0l)

		then:
		output == expectedResult

		where:
		update 		 		| expectedResult
		new Object() 		| 1
		[1, 2, 3].toArray() | 3
		[1: 1, 2: 2, 3: 3]  | 3
		[1, 2, 3] as List 	| 3
	}

	@Unroll
	def "check applyAverage"(def update, def currentCount, def currentAverage, def expectedResult) {
		when:
		def result = FunctionHelper.apply(update, currentCount, currentAverage)

		then:
		result == expectedResult

		where:
		update	| currentCount	| currentAverage	| expectedResult
		21		| 10			| 10				| 11
		21l		| 10l			| 10l				| 11l
	}

	@Unroll
	def "check applyAverage for objects"(def update, def currentCount, def currentAverage, def expectedResult) {
		when:
		FunctionHelper.apply(update, currentCount, currentAverage)

		then:
		currentAverage.get() == expectedResult

		where:
		update	| currentCount			| currentAverage		| expectedResult
		21		| new AtomicInteger(10) | new AtomicInteger(10) | 11
		21l		| new AtomicLong(10) 	| new AtomicLong(10) 	| 11
	}

	@Unroll
	def "check apply for AtomicInt"(AggregationType type, int current, int update, int result) {
		when:
		AtomicInteger currentObject = new AtomicInteger(current)
		FunctionHelper.apply(type, update, currentObject)

		then:
		currentObject.get() == result

		where:
		type 					| current | update | result
		AggregationType.MAX		| 10	  | 20	   | 20
		AggregationType.MAX		| 20	  | 10	   | 20
		AggregationType.MIN		| 10	  | 20	   | 10
		AggregationType.MIN		| 20	  | 10	   | 10
		AggregationType.SUM		| 10	  | 20	   | 30
		AggregationType.COUNT 	| 10	  | 20	   | 11
	}

	@Unroll
	def "check apply for AtomicLong"(AggregationType type, long current, long update, long result) {
		when:
		AtomicLong currentObject = new AtomicLong(current)
		FunctionHelper.apply(type, update, currentObject)

		then:
		currentObject.get() == result

		where:
		type 					| current | update | result
		AggregationType.MAX		| 10	  | 20	   | 20
		AggregationType.MAX		| 20	  | 10	   | 20
		AggregationType.MIN		| 10	  | 20	   | 10
		AggregationType.MIN		| 20	  | 10	   | 10
		AggregationType.SUM		| 10	  | 20	   | 30
		AggregationType.COUNT 	| 10	  | 20	   | 11
	}

	@Unroll
	def "check count for object with AtomicLong result"(Object update, int expectedResult) {
		when:
		def current = new AtomicLong()
		FunctionHelper.apply(AggregationType.COUNT, update, current)

		then:
		current.get() == expectedResult

		where:
		update 		 		| expectedResult
		new Object() 		| 1
		[1, 2, 3].toArray() | 3
		[1: 1, 2: 2, 3: 3]  | 3
		[1, 2, 3] as List 	| 3
	}
}
