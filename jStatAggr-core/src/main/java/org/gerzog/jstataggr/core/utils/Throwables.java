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
