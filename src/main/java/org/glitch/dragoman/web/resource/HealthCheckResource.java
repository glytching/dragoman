package org.glitch.dragoman.web.resource;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

import static org.glitch.dragoman.web.WebServerUtils.withApplicationName;

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
        router.get(withApplicationName("healthcheck")).blockingHandler(routingContext -> {
            Map<String, HealthCheck.Result> results = healthCheckRegistry.runHealthChecks();

            routingContext.response().end(Json.encodePrettily(results));
        });
    }
}