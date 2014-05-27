package org.gerzog.jstataggr.core.impl;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.gerzog.jstataggr.core.IStatisticsManager;
import org.gerzog.jstataggr.core.internal.impl.AbstractStatisticsHandler;

/**
 * Asynchronious Statistics handler that uses Executor to schedule statistics
 * update events
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class AsyncStatisticsHandler extends AbstractStatisticsHandler {

	private static final int DEFAULT_THREAD_NUMBER = 1;

	private int threadNumber;

	private ExecutorService updateExecutor;

	public AsyncStatisticsHandler(final int threadNumber) {
		super();

		this.threadNumber = threadNumber;
	}

	public AsyncStatisticsHandler() {
		this(DEFAULT_THREAD_NUMBER);
	}

	public AsyncStatisticsHandler(final IStatisticsManager manager, final int threadNumber) {
		super(manager);

		this.threadNumber = threadNumber;
	}

	public AsyncStatisticsHandler(final IStatisticsManager manager) {
		this(manager, DEFAULT_THREAD_NUMBER);
	}

	@Override
	protected void handleStatistics(final Runnable action) {
		notNull(updateExecutor, "Executor was not initialized. Please call method initialize().");

		updateExecutor.submit(action);
	}

	public void setThreadNumber(final int threadNumber) {
		this.threadNumber = threadNumber;
	}

	@PostConstruct
	public void initialize() {
		isTrue(threadNumber > 0, "ThreadNumber can be in rage %s..%s, but input value is %s", 1, Integer.MAX_VALUE, threadNumber);

		updateExecutor = Executors.newFixedThreadPool(threadNumber);
	}

}
