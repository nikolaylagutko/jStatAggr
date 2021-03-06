/**
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
package org.gerzog.jstataggr.core.functions;

import java.util.concurrent.atomic.LongAccumulator;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class MinAccumulator extends LongAccumulator {

	private static final long serialVersionUID = 6198909126811980974L;

	public MinAccumulator() {
		super((left, right) -> Math.min(left, right), Long.MAX_VALUE);
	}

}
