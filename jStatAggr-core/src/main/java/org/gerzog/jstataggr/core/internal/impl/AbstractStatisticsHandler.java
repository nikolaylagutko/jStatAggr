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
package org.gerzog.jstataggr.core.internal.impl;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.gerzog.jstataggr.IStatisticsFilter;
import org.gerzog.jstataggr.IStatisticsHandler;
import org.gerzog.jstataggr.IStatisticsManager;
import org.gerzog.jstataggr.IStatisticsWriter;
import org.gerzog.jstataggr.annotations.StatisticsEntry;

/**
 * Abstract implementation of Statistcs Handler
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public abstract class AbstractStatisticsHandler implements IStatisticsHandler {

	private static final String NO_ANNOTATION_MESSAGE = "%s class is not marked with mandatory annotation @StatisticsEntry";
	private static final String INPUT_PARAMETER_NULL_MESSAGE = "StatisticsEntry cannot be null";

	private IStatisticsManager manager;

	private Collection<IStatisticsWriter> writers;

	protected AbstractStatisticsHandler() {
		this(null);
	}

	protected AbstractStatisticsHandler(final IStatisticsManager manager) {
		this.manager = manager;
	}

	public void setManager(final IStatisticsManager manager) {
		this.manager = manager;
	}

	@Override
	public void handleStatistics(final Object statisticsEntry) {
		notNull(statisticsEntry, INPUT_PARAMETER_NULL_MESSAGE);

		final Class<?> clazz = statisticsEntry.getClass();

		final StatisticsEntry annotation = clazz
				.getAnnotation(StatisticsEntry.class);

		final String className = clazz.getSimpleName();

		isTrue(annotation != null, NO_ANNOTATION_MESSAGE, className);

		handleStatistics(() -> updateStatistics(statisticsEntry,
				getStatisticsName(annotation, className)));
	}

	private void updateStatistics(final Object statisticsEntry,
			final String statisticsName) {
		notNull(manager, "Statistics Manager cannot be null");

		manager.updateStatistics(statisticsEntry, statisticsName);
	}

	private String getStatisticsName(final StatisticsEntry entry,
			final String className) {
		final String entryName = entry.value();

		return StringUtils.isEmpty(entryName) ? className : entryName;
	}

	protected abstract void handleStatistics(Runnable action);

	@Override
	public void writeStatistics(final boolean cleanup) throws Exception {
		writeStatistics(null, (key) -> true, cleanup);

	}

	@Override
	public void writeStatistics(final String statisticsName,
			final boolean cleanup) throws Exception {
		writeStatistics(statisticsName, (key) -> true, cleanup);
	}

	@Override
	public void writeStatistics(final String statisticsName,
			final IStatisticsFilter filter, final boolean cleanup)
					throws Exception {
		notNull(manager, "Statistics Manager cannot be null");

		if (writers != null) {
			for (final Entry<String, Collection<Object>> entry : manager
					.collectStatistics(statisticsName, filter, cleanup)
					.entrySet()) {
				for (final IStatisticsWriter writer : writers) {
					writer.writeStatistics(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	@Override
	public void setStatisticsWriters(final Collection<IStatisticsWriter> writers) {
		this.writers = writers;
	}

}
