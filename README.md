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

Next step is to instantiate this bean, set properties and provide it to IStatisticsHandler

```java
IStatisticsHandler handler = createHandler(); //instantiating handler

Statistics statistics = collectStatistics(); //collecting statistics info

handler.handleStatistics(statistics);
```

That's all!

### Include jStatAggr in my project

jStatAggr is available on Bintray jcenter repository. Here is example of simple gradle script to include this library to your project

```gradle
repositories {
	jcenter()
}

dependencies {
	compile group: 'org.gerzog.jstataggr',	name: 'jstataggr-core',	version: JSTATAGGR_VERSION
}
```

jStatAggr have two parallel branches - one is for full Java8 support and another one is adapted for Java7, both versions available on jcenter. 
Current versions are: _0.1.0-java7_ and _0.1.0-java8_

### View collected statistics

To view collected statistics jStatAggr provided interface called IStatisticsWriter. At current moment only CSV export implemented (see below).

Main workflow for statistics writing - is to define implementation of IStatisticsWriter, register it in IStatisticsHandler. After this just 
call one of methods in IStatisticsHandler
* writeStatistics(boolean cleanup) - collects and send to writers all available statistics
* writeStatistics(String statisticsName, boolean cleanup) - collects and send to writers only statistics by corresponding name (e.g. only for 
one class marked by @StatisticsEntry)
* writeStatistics(String statisticsName, IStatisticsFilter filter, boolean cleanup) - collects and sent to writers statistics by name with additional
filtering
_cleanup_ parameter of all methods forces (if set to _true_) handler to remove all statistics that was exported, e.g. sent to writers  

### EL support (jstataggr-el/jstataggr-spel)

In some cases you need to aggregate not a raw value but formatted/updated by some rules. An easy example - each of Statistics piece will have a 
timestamp property as a key

```java
@StatisticsEntry
public class Statistics {

	@StatisticsKey
	private long timestamp;
	
	@Aggregated(AggregationType.SUM)
	private long value;
	
	public Statistics() {
		this.timestamp = System.currentTimeMillis()
	}
	
	//getters and setters
}
```

But in a result we'd like to see per-minute aggregated statistics. In this case we can add extra rules that will be applied for value of a bean field:

```java
	@StatisticsKey
	@Expression("#this / 1000 / 60")
	private long timestamp;
```

Value of @Expression will be processed with EL-engine provided by one of extra-dependencies (that should be applied to project):
* jstataggr-spel - SPeL (Spring Expression Language) engine, _#this_ variable will be binded to original value of field.
* jstataggr-juel - JUEL (Java Unified Expression Language) engine, _this_ variable will be binded to original value of field.

### CSV writer (jstataggr-csv module)

## Code Status

* [![Build Status](https://travis-ci.org/nikolaylagutko/jStatAggr.svg?branch=master)](https://travis-ci.org/rails/rails)
 
