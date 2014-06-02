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
public class StatisticsOne {

	@Aggregated({ AggregationType.MIN, AggregationType.MAX, AggregationType.SUM })
	private int value;

	@StatisticsKey
	private String key;

	@StatisticsKey
	private String anotherKey;

	public int getValue() {
		return value;
	}

	public void setValue(final int value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getAnotherKey() {
		return anotherKey;
	}

	public void setAnotherKey(final String anotherKey) {
		this.anotherKey = anotherKey;
	}

}
