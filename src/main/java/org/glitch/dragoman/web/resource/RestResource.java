package org.glitch.dragoman.web.resource;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public interface RestResource {

    void configure(Vertx vertx, HttpServer httpServer, Router router);
}
