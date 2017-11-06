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
package org.glytching.dragoman.web.resource;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

import static org.glytching.dragoman.web.WebServerUtils.withApplicationName;

/**
 * A {@link RestResource} which runs all configured {@link HealthCheck} instances and returns the results in JSON format.
 */
public class HealthCheckResource implements RestResource {

    private final HealthCheckRegistry healthCheckRegistry;

    @Inject
    public HealthCheckResource(Set<HealthCheck> healthChecks) {
        this.healthCheckRegistry = new HealthCheckRegistry();
        for (HealthCheck healthCheck : healthChecks) {
            healthCheckRegistry.register(healthCheck.getClass().getSimpleName(), healthCheck);
        }
    }

    @Override
    public void configure(Vertx vertx, HttpServer httpServer, Router router) {
        // using blockingHandler since we cannot be sure that all health cheks are non-blocking
        router.get(withApplicationName("healthcheck")).blockingHandler(routingContext -> {
            Map<String, HealthCheck.Result> results = healthCheckRegistry.runHealthChecks();

            routingContext.response().end(Json.encodePrettily(results));
        });
    }
}