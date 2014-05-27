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
package org.gerzog.jstataggr.core.impl;

import org.gerzog.jstataggr.core.IStatisticsManager;
import org.gerzog.jstataggr.core.internal.impl.AbstractStatisticsHandler;

/**
 * Statistics Handler that runs in Synchronized Mode
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class SynchronizedStatisticsHandler extends AbstractStatisticsHandler {

	public SynchronizedStatisticsHandler(final IStatisticsManager statistcsManager) {
		super(statistcsManager);
	}

	public SynchronizedStatisticsHandler() {
		super();
	}

	@Override
	protected void handleStatistics(final Runnable action) {
		action.run();
	}

}
