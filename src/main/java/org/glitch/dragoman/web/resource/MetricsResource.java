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