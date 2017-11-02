package org.glitch.dragoman.web.resource;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import static org.glitch.dragoman.web.WebServerUtils.plainContentType;
import static org.glitch.dragoman.web.WebServerUtils.withApplicationName;

public class PingResource implements RestResource {

    @Override
    public void configure(Vertx vertx, HttpServer httpServer, Router router) {
        router.get(withApplicationName("ping")).handler(routingContext -> plainContentType(routingContext.response()).end("pong"));
    }
}