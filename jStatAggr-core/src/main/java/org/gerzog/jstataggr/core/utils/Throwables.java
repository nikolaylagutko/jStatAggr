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
package org.gerzog.jstataggr.core.utils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class Throwables {

	@FunctionalInterface
	public interface VoidCallable {
		void call() throws Throwable;
	}

	@FunctionalInterface
	public interface Callable<T> {
		T call() throws Throwable;
	}

	@FunctionalInterface
	public static interface ExceptionHandler<E> {
		E handle(Throwable t);
	}

	private Throwables() {

	}

	public static void propogate(VoidCallable callable) throws RuntimeException {
		propogate(() -> {
			callable.call();
			return 0;
		}, RuntimeException::new);
	}

	public static <T> T propogate(Callable<T> callable) throws RuntimeException {
		return propogate(callable, RuntimeException::new);
	}

	public static <T, E extends Throwable> T propogate(Callable<T> callable,
			ExceptionHandler<E> handler) throws E {
		try {
			return callable.call();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw handler.handle(e);
		}
	}

}
