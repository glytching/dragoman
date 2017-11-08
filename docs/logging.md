Logging
======

Dragoman uses the [Logback](https://logback.qos.ch/) library. It produces three categories of logging:

* Standard: application logs, flow of control, exceptions and warnings. Log levels are chosen using the following simple rules of thumb:
  * INFO: thread flow, entry/exit, system startup details etc
  * WARN: potentially harmful situations
  * ERROR: failure events that might still allow the application to continue running
* Metrics: a pairing of timestamp and the JSON payload from the metrics subsystem (see [metrics](metrics.md) for more details).
* Embedded Mongo: all logging for the embedded Mongo instance are directed to a distinct logger 

#### File vs. Console

When Dragoman is not run with the `-Dlog.dir` parameter **all** logs are written to console. When Dragoman is run with the `-Dlog.dir` parameter **all** logs are written to the following files:

* Standard: `dragoman.log`
* Metrics: `dragoman-metrics.log`
* Embedded Mongo: `dragoman-embedded-mongo.log`

These files are subject to rolling using Logback's [SizeAndTimeBasedRollingPolicy](https://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy). For more details see `logback.xml`.


#### Log Event Pattern

When writing to console all log events are subject to this pattern:

> %d{ISO8601}|[%thread]|%level|%logger{36}|%msg%n

For example:

> 2017-11-07 20:55:31,093|[vertx-worker-2]|INFO|o.g.d.web.resource.DatasetResource|Getting dataset for id: 5a021d47c786a075ba432cca

The value of `%logger{36}` is always `metrics-logger` for metrics log events and is always `embedded-mongo` for embedded Mongo log events.

When writing to file, there are separate patterns.

The `metrics` log pattern is: 

> %d{ISO8601}|%msg%n

For example:

> 2017-11-08 08:51:23,035|{"delete-requests":{...}}

The `standard` and `embedded mongo` log pattern is: 

> %d{ISO8601}|[%thread]|%level|%logger{36}|%msg%n

For example:

> 2017-11-08 08:50:35,925|[vert.x-eventloop-thread-0]|INFO|o.g.dragoman.DragomanVerticle|Deployed EmbeddedMongoVerticle verticle with id: a01f9832-a385-4603-ac4b-bfb6a7965a1b

> 2017-11-08 08:50:18,751|[Thread-5]|INFO|embedded-mongo|2017-11-08T08:50:18.746+0000 I CONTROL  [initandlisten] db version v3.4.1[embedded-mongo output] 