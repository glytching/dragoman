/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glitch.dragoman.web;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetricsFacade {
    private static final Logger logger = LoggerFactory.getLogger(MetricsFacade.class);
    private static final Logger metricsLogger = LoggerFactory.getLogger("metrics-logger");

    private final HttpServer httpServer;
    private final MetricsService metricsService;

    public MetricsFacade(Vertx vertx, HttpServer httpServer, int publicationPeriodInMillis) {
        this.httpServer = httpServer;
        this.metricsService = MetricsService.create(vertx);

        logger.info("Scheduling metrics publication every {}ms", publicationPeriodInMillis);

        // ensure that the metrics publication does *not* happen on an event loop thread
        vertx.setPeriodic(publicationPeriodInMillis, event -> vertx.executeBlocking(event1 -> {
            JsonObject metrics = metricsService.getMetricsSnapshot(httpServer);
            if (metrics != null) {
                metricsLogger.info(metrics.encode());
            }
            event1.complete();
        }, (Handler<AsyncResult<Void>>) event12 -> {
            // no-op
        }));
    }

    public JsonObject getAll() {
        return metricsService.getMetricsSnapshot(httpServer);
    }

    public JsonObject getByName(String name) {
        return metricsService.getMetricsSnapshot(httpServer).getJsonObject(name);
    }

    public JsonObject getByFilter(String regex) {
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // read all metrics
        Map<String, Object> filtered = getAll().getMap().entrySet()
                // stream them and filter by applying the regex to the metrics key
                .stream().filter(item -> pattern.matcher(item.getKey()).find())
                // collect the results into a map
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new JsonObject(filtered);
    }
}