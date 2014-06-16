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
package org.gerzog.jstataggr.writers.csv.utils;

import static org.gerzog.jstataggr.core.utils.Throwables.propogate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class BeanUtils {

	private BeanUtils() {

	}

	public static List<String> getBeanProperties(final Class<?> clazz,
			final boolean includeSuperclassFields) {
		final List<String> result = new ArrayList<>();

		propogate(() -> {
			for (final Field field : clazz.getDeclaredFields()) {
				final String name = field.getName();

				if ((field != null)
						&& (!field.isSynthetic())
						&& (includeSuperclassFields || !isSuperclassField(
								clazz, field))) {
					result.add(name);
				}
			}
		});

		return result;
	}

	private static boolean isSuperclassField(final Class<?> clazz,
			final Field field) {
		return !field.getDeclaringClass().equals(clazz);
	}

}
