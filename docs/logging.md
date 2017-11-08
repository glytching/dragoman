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

```
%d{ISO8601}|[%thread]|%level|%logger{36}|%msg%n
```

For example:

```
2017-11-07 20:55:31,093|[vertx-worker-2]|INFO|o.g.d.web.resource.DatasetResource|Getting dataset for id: 5a021d47c786a075ba432cca
```

The value of `%logger{36}` is always `metrics-logger` for metrics log events and is always `embedded-mongo` for embedded Mongo log events.

When writing to file, there are separate patterns.

The `metrics` log pattern is: 

```
%d{ISO8601}|%msg%n
```

For example:

```
2017-11-08 10:52:13,597|[vertx-worker-14]|INFO|metrics-logger|{"delete-requests":{"type":"timer","oneSecondRate":0,"count":2,"meanRate":5.651240286471607E-4,"oneMinuteRate":0.006588432391127455,"fiveMinuteRate":0.004817573805080151,"fifteenMinuteRate":0.0019940965306243976,"rate":"events/seconds","min":0.568813,"max":2.654515,"mean":1.5413782715399287,"stddev":1.040479756927465,"median":0.568813,"75%":2.654515,"95%":2.654515,"98%":2.654515,"99%":2.654515,"99.9%":2.654515,"durationRate":"milliseconds"},"post-requests":{"type":"timer","oneSecondRate":0,"count":1010,"meanRate":0.2853876842691808,"oneMinuteRate":0.41253311958784916,"fiveMinuteRate":0.405127669167274,"fifteenMinuteRate":0.37749033299449974,"rate":"events/seconds","min":0.465146,"max":1132.3040799999999,"mean":1.4810624675481785,"stddev":10.448334693765243,"median":0.6079129999999999,"75%":0.7071529999999999,"95%":0.8448559999999999,"98%":4.88366,"99%":4.88366,"99.9%":147.55389399999999,"durationRate":"milliseconds"},"patch-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"trace-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"options-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"get-requests":{"type":"timer","oneSecondRate":0,"count":46,"meanRate":0.012997856216376192,"oneMinuteRate":0.04309006538297238,"fiveMinuteRate":0.018438458557596852,"fifteenMinuteRate":0.009090660326922247,"rate":"events/seconds","min":1.28453,"max":1523.885532,"mean":394.02542750040436,"stddev":659.4165511681371,"median":5.040983,"75%":1523.885532,"95%":1523.885532,"98%":1523.885532,"99%":1523.885532,"99.9%":1523.885532,"durationRate":"milliseconds"},"requests":{"type":"timer","oneSecondRate":0,"count":1058,"meanRate":0.29895025014330084,"oneMinuteRate":0.46221161736194893,"fiveMinuteRate":0.4283837020063475,"fifteenMinuteRate":0.388575216198518,"rate":"events/seconds","min":0.465146,"max":1523.885532,"mean":35.50451362266913,"stddev":223.5697972878225,"median":0.6376999999999999,"75%":0.754371,"95%":5.040983,"98%":1523.885532,"99%":1523.885532,"99.9%":1523.885532,"durationRate":"milliseconds"},"bytes-written":{"type":"histogram","count":1058,"min":0.0,"max":25722.0,"mean":85.80639602246818,"stddev":317.30043759573994,"median":0.0,"75%":0.0,"95%":1393.0,"98%":1396.0,"99%":1414.0,"99.9%":1421.0},"open-connections.127.0.0.1":{"type":"counter","count":6},"connect-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"exceptions":{"type":"counter","count":0},"head-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"responses-5xx":{"type":"meter","oneSecondRate":0,"count":1,"meanRate":2.825619468751102E-4,"oneMinuteRate":0.011458136074669095,"fiveMinuteRate":0.0030925140804588976,"fifteenMinuteRate":0.0010836790736504383,"rate":"events/seconds"},"open-netsockets":{"type":"counter","count":6},"responses-3xx":{"type":"meter","oneSecondRate":0,"count":3,"meanRate":8.476857634047931E-4,"oneMinuteRate":8.459008066672269E-21,"fiveMinuteRate":7.887128721301981E-7,"fifteenMinuteRate":1.1261800287194044E-4,"rate":"events/seconds"},"responses-4xx":{"type":"meter","oneSecondRate":0,"count":1,"meanRate":2.825620493398995E-4,"oneMinuteRate":8.758231871184803E-28,"fiveMinuteRate":2.9307054931096828E-8,"fifteenMinuteRate":2.293264692190331E-5,"rate":"events/seconds"},"open-websockets":{"type":"counter","count":0},"bytes-read":{"type":"histogram","count":1060,"min":0.0,"max":222.0,"mean":25.642202129103666,"stddev":31.97020280386869,"median":23.0,"75%":23.0,"95%":23.0,"98%":222.0,"99%":222.0,"99.9%":222.0},"other-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"},"responses-1xx":{"type":"meter","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds"},"responses-2xx":{"type":"meter","oneSecondRate":0,"count":1053,"meanRate":0.2975378136805474,"oneMinuteRate":0.4507534812872798,"fiveMinuteRate":0.42529036990596164,"fifteenMinuteRate":0.3873559864750735,"rate":"events/seconds"},"connections":{"type":"timer","count":12,"meanRate":0.003390726485721929,"oneMinuteRate":1.8097858870773388E-19,"fiveMinuteRate":4.1960058924380295E-6,"fifteenMinuteRate":4.8175682219201875E-4,"rate":"events/seconds","min":2393.4160629999997,"max":115509.33133599999,"mean":109000.01959362236,"stddev":25980.56979125462,"median":115258.221786,"75%":115483.44445099999,"95%":115483.44445099999,"98%":115483.44445099999,"99%":115483.44445099999,"99.9%":115483.44445099999,"durationRate":"milliseconds"},"put-requests":{"type":"timer","oneSecondRate":0,"count":0,"meanRate":0.0,"oneMinuteRate":0.0,"fiveMinuteRate":0.0,"fifteenMinuteRate":0.0,"rate":"events/seconds","min":0.0,"max":0.0,"mean":0.0,"stddev":0.0,"median":0.0,"75%":0.0,"95%":0.0,"98%":0.0,"99%":0.0,"99.9%":0.0,"durationRate":"milliseconds"}}
```

The `standard` and `embedded mongo` log pattern is: 

```
%d{ISO8601}|[%thread]|%level|%logger{36}|%msg%n
```

For example:

```
2017-11-08 08:50:35,925|[vert.x-eventloop-thread-0]|INFO|o.g.dragoman.DragomanVerticle|Deployed EmbeddedMongoVerticle verticle with id: a01f9832-a385-4603-ac4b-bfb6a7965a1b
2017-11-08 08:50:18,751|[Thread-5]|INFO|embedded-mongo|2017-11-08T08:50:18.746+0000 I CONTROL  [initandlisten] db version v3.4.1[embedded-mongo output] 
```