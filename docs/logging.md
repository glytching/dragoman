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

> 2017-11-08 08:51:23,035|{"delete-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"post-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"patch-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"trace-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"options-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"get-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"bytes-written":{"type":"histogram","count":0,"min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0},"connect-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"head-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"exceptions":{"type":"counter","count":0},"responses-5xx":{"type":"meter","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds"},"open-netsockets":{"type":"counter","count":0},"responses-3xx":{"type":"meter","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds"},"responses-4xx":{"type":"meter","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds"},"open-websockets":{"type":"counter","count":0},"bytes-read":{"type":"histogram","count":0,"min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0},"other-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"responses-1xx":{"type":"meter","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds"},"responses-2xx":{"type":"meter","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds"},"connections":{"type":"timer","count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"put-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"}}

The `standard` and `embedded mongo` log pattern is: 

> %d{ISO8601}|[%thread]|%level|%logger{36}|%msg%n

For example:

> 2017-11-08 08:50:35,925|[vert.x-eventloop-thread-0]|INFO|o.g.dragoman.DragomanVerticle|Deployed EmbeddedMongoVerticle verticle with id: a01f9832-a385-4603-ac4b-bfb6a7965a1b

> 2017-11-08 08:50:18,751|[Thread-5]|INFO|embedded-mongo|2017-11-08T08:50:18.746+0000 I CONTROL  [initandlisten] db version v3.4.1[embedded-mongo output] 