## Statistics Aggregator

## Description

jStatAggr (Java Statistics Aggregator) is a number of libaries to simplify aggregation of statistics data. 

Main goal is to provide easy-to-use and simple way to handle most-commmon used aggregations (min/max values etc.) and show this results back to user.

### Main functionality (jstataggr-core module)

This library provides annotation-based way to handle statistics. To have all benefits of jStatAggr you just need to do two simple things
* define a bean that represents a piece of data to be aggregated
* define implementation of IStatisticsHandler

Let's have a look to simple example of statistics bean. Imagine you need to have statistics for some http proxy that should show you 
min/max/average time of request handling for each processed URL. In this case smallest piece of data will contain URL and duration of request:

```java
public class Statistics {

	private String url;
	
	private long duration;
	
	//getters and setters

}
```

Than we should add some annotations to have a bean that jStatAggr can understand:

```java
@StatisticsEntry("proxy_statistics")
public class Statistics {
	@StatisticsKey
	private String url;
	
	@Aggregated({ AggregationType.MIN, AggregationType.MAX, AggregationType.AVERAGE })
	private long duration;
	
	//getters and setters
}
```

Let's have a look to used annotaion:
* @StatisticsEntry - mark a bean to be used as piece of statistics data. Value of this 
annotation is not mandatory and if it missing it will be used bean class name.
* @StatisticsKey - marks a field to be used key of collected statistics. In the example 
above it will mean that collected statistics for duration will be separated for each URL.
Class can have more than one @StatisticsKey.
* @Aggregated - defines number of aggregation types that should be applied to value of this field

### EL support (jstataggr-el/jstataggr-spel)

### CSV writer (jstataggr-csv module)

## Code Status

* [![Build Status](https://travis-ci.org/nikolaylagutko/jStatAggr.svg?branch=master)](https://travis-ci.org/rails/rails)
 
