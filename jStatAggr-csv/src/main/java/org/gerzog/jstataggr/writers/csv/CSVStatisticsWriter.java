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
package org.gerzog.jstataggr.writers.csv;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.gerzog.jstataggr.IStatisticsWriter;
import org.gerzog.jstataggr.writers.csv.utils.BeanUtils;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class CSVStatisticsWriter implements IStatisticsWriter {

	private static final String SIMPLE_FORMATTED_FILENAME = "{0}.csv";

	private static final String INDEXED_FORMATTED_FILENAME = "{0}-{1}.csv";

	protected static class WritersCache {

		private final Map<String, Map<String, ICsvBeanWriter>> cache = new HashMap<>();

		private final Map<ICsvBeanWriter, String[]> columnsCache = new HashMap<>();

		public void registerColumns(final ICsvBeanWriter writer,
				final String[] columns) {
			columnsCache.put(writer, columns);
		}

		public String[] getColumns(final ICsvBeanWriter writer) {
			return columnsCache.get(writer);
		}

		public ICsvBeanWriter getWriter(final String statisticsName,
				final String filename) {
			final Map<String, ICsvBeanWriter> statisticsWriters = cache
					.get(statisticsName);

			ICsvBeanWriter result = null;

			if (statisticsWriters != null) {
				result = statisticsWriters.get(filename);
			}

			return result;
		}

		public void registerWriter(final String statisticsName,
				final String filename, final ICsvBeanWriter writer) {
			Map<String, ICsvBeanWriter> statisticsWriters = cache
					.get(statisticsName);

			if (statisticsWriters == null) {
				statisticsWriters = new HashMap<>();
				cache.put(statisticsName, statisticsWriters);
			}

			statisticsWriters.put(filename, writer);
		}

		public List<ICsvBeanWriter> getAllWriters() {
			final List<ICsvBeanWriter> result = new ArrayList<>();

			for (final Map<String, ICsvBeanWriter> writers : cache.values()) {
				result.addAll(writers.values());
			}

			return result;
		}
	}

	private List<ICSVWriterConfig> configurations;

	public CSVStatisticsWriter() {
		this(new ArrayList<ICSVWriterConfig>(0));
	}

	public CSVStatisticsWriter(final List<ICSVWriterConfig> configurations) {
		this.configurations = configurations;
	}

	public void setConfigurations(final List<ICSVWriterConfig> configurations) {
		notNull(configurations, "Configurations cannot be null");

		this.configurations = configurations;
	}

	@Override
	public void writeStatistics(final String statisticsName,
			final Collection<Object> statisticsData) throws IOException {
		final List<ICSVWriterConfig> appliedConfigs = defineConfigs(statisticsName);

		final WritersCache writers = new WritersCache();

		if (!appliedConfigs.isEmpty()) {
			writeStatistics(appliedConfigs, statisticsName, statisticsData,
					writers);
		}

		finishUp(writers);
	}

	protected void finishUp(final WritersCache cache) {
		for (final ICsvBeanWriter writer : cache.getAllWriters()) {
			IOUtils.closeQuietly(writer);
		}
	}

	protected List<ICSVWriterConfig> defineConfigs(final String statisticsName) {
		final List<ICSVWriterConfig> result = new ArrayList<>();

		for (final ICSVWriterConfig config : configurations) {
			if (config.isApplied(statisticsName)) {
				result.add(config);
			}
		}

		return result;
	}

	protected void writeStatistics(final List<ICSVWriterConfig> configurations,
			final String statisticsName,
			final Collection<Object> statisticsData, final WritersCache writers)
					throws IOException {
		for (final Object object : statisticsData) {
			for (final ICSVWriterConfig config : configurations) {
				final ICsvBeanWriter writer = prepareWriter(config,
						statisticsName, object, writers);

				final String[] columns = defineColumnNames(config, object,
						writer, writers);

				writer.write(object, columns);
			}
		}
	}

	protected ICsvBeanWriter prepareWriter(final ICSVWriterConfig config,
			final String statisticsName, final Object object,
			final WritersCache cache) throws IOException {
		final String filename = config.getFilename(statisticsName, object);

		ICsvBeanWriter writer = cache.getWriter(statisticsName, filename);

		if (writer == null) {
			writer = createWriter(filename);
			writer.writeHeader(defineColumnNames(config, object, writer, cache));

			cache.registerWriter(statisticsName, filename, writer);
		}

		return writer;
	}

	protected String[] defineColumnNames(final ICSVWriterConfig config,
			final Object object, final ICsvBeanWriter writer,
			final WritersCache cache) {
		String[] result = cache.getColumns(writer);

		if (result == null) {
			result = getFieldNames(object.getClass(), config);

			cache.registerColumns(writer, result);
		}

		return result;
	}

	protected ICsvBeanWriter createWriter(final String fileName)
			throws IOException {
		String formattedFilename = MessageFormat.format(
				SIMPLE_FORMATTED_FILENAME, fileName);

		File outputFile = new File(formattedFilename);

		int index = 1;

		while (outputFile.exists()) {
			formattedFilename = MessageFormat.format(
					INDEXED_FORMATTED_FILENAME, fileName, index++);
			outputFile = new File(formattedFilename);
		}

		return new CsvBeanWriter(new FileWriter(outputFile),
				CsvPreference.STANDARD_PREFERENCE);
	}

	protected String[] getFieldNames(final Class<?> clazz,
			final ICSVWriterConfig config) {
		final List<String> result = new ArrayList<>();

		for (final String property : BeanUtils.getBeanProperties(clazz, false)) {
			if ((config.getExcludedFields() == null)
					|| !config.getExcludedFields().contains(property)) {
				result.add(property);
			}
		}

		final Comparator<String> comparator = config.getFieldComparator() == ICSVWriterConfig.DEFAULT_COMPARATOR ? null
				: config.getFieldComparator();

		Collections.sort(result, comparator);

		final String[] resultArray = new String[result.size()];
		return result.toArray(resultArray);
	}
}
