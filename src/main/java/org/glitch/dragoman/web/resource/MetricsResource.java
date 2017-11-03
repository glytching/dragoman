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
package org.glitch.dragoman.web.resource;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.web.MetricsFacade;

import javax.inject.Inject;

import static org.glitch.dragoman.web.WebServerUtils.withApplicationName;

/**
 * A {@link RestResource} which provides access to Vert.x's metrics data.
 */
public class MetricsResource implements RestResource {

    private MetricsFacade metricsFacade;

    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    public MetricsResource(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void configure(Vertx vertx, HttpServer httpServer, Router router) {
        router.get(withApplicationName("metrics")).blockingHandler(routingContext -> {
            HttpServerRequest request = routingContext.request();
            if (request.params().contains("regex")) {
                // get a set of metric(s), identified by regex
                String regex = routingContext.request().getParam("regex");

                JsonObject filtered = metricsFacade.getByFilter(regex);

                routingContext.response().end(filtered.encodePrettily());
            } else {
                // get all metrics
                routingContext.response().end(metricsFacade.getAll().encodePrettily());
            }
        });

        // get a named metrics
        router.get(withApplicationName("metrics/:name")).blockingHandler(routingContext -> {
            JsonObject filtered = metricsFacade.getByName(routingContext.request().getParam("name"));

            routingContext.response().end(filtered.encodePrettily());
        });

        this.metricsFacade = new MetricsFacade(vertx, httpServer,
                applicationConfiguration.getMetricsPublicationPeriod());
    }
}