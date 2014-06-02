package org.gerzog.jstataggr.sample;

import org.gerzog.jstataggr.AggregationType;
import org.gerzog.jstataggr.annotations.Aggregated;
import org.gerzog.jstataggr.annotations.StatisticsEntry;
import org.gerzog.jstataggr.annotations.StatisticsKey;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
@StatisticsEntry("sample_statistics_one")
public class StatisticsTwo {

	@Aggregated({ AggregationType.MIN })
	private int value1;

	@Aggregated({ AggregationType.MAX })
	private int value2;

	@Aggregated({ AggregationType.SUM })
	private int value3;

	@StatisticsKey
	private String key;

	@StatisticsKey
	private int anotherKey;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public int getAnotherKey() {
		return anotherKey;
	}

	public void setAnotherKey(final int anotherKey) {
		this.anotherKey = anotherKey;
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(final int value1) {
		this.value1 = value1;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(final int value2) {
		this.value2 = value2;
	}

	public int getValue3() {
		return value3;
	}

	public void setValue3(final int value3) {
		this.value3 = value3;
	}

}
