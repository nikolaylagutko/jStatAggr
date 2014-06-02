package org.gerzog.jstataggr.core.utils;

import javassist.CtField.Initializer;

import org.gerzog.jstataggr.AggregationType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class InitializerUtils {

	private static final int ZERO = 0;

	private InitializerUtils() {

	}

	public static Initializer getInitializer(final Class<?> type, final AggregationType aggregation) {
		switch (aggregation) {
		case MIN:
			return getMinInitializer(type);
		case MAX:
			return getMaxInitializer(type);
		case SUM:
			return getSumInitializer(type);
		default:
			throw new IllegalArgumentException("Initializer for <" + aggregation + "> is not yet defined");
		}
	}

	private static Initializer getMaxInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(Integer.MIN_VALUE);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(Long.MIN_VALUE);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

	private static Initializer getMinInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(Integer.MAX_VALUE);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(Long.MAX_VALUE);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

	private static Initializer getSumInitializer(final Class<?> type) {
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return Initializer.constant(ZERO);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Initializer.constant(ZERO);
		}

		throw new IllegalArgumentException("Initialzer for <" + type.getSimpleName() + "> class is not yet defined");
	}

}
