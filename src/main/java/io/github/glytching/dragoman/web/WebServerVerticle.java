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
package io.github.glytching.dragoman.web;

import com.google.common.collect.Maps;
import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.web.exception.AccessDeniedException;
import io.github.glytching.dragoman.web.resource.RestResource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JolokiaServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * A {@link io.vertx.core.Verticle} instance for our embedded web server.
 */
public class WebServerVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(WebServerVerticle.class);

    private final Set<RestResource> restResources;
    private final ApplicationConfiguration applicationConfiguration;
    private HttpServer httpServer;
    private JolokiaServer jolokiaServer;

    @Inject
    public WebServerVerticle(
            Set<RestResource> restResources, ApplicationConfiguration applicationConfiguration) {
        this.restResources = restResources;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void start(Future<Void> future) {
        this.httpServer = vertx.createHttpServer(createOptions());

        Router router = router();

        httpServer.requestHandler(router::accept);
        httpServer.listen(
                result -> {
                    if (result.succeeded()) {
                        if (applicationConfiguration.isJolokiaEnabled()) {
                            startJolokia();
                        }
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }

    @Override
    public void stop(Future<Void> future) {
        if (jolokiaServer != null) {
            try {
                jolokiaServer.stop();
            } catch (Exception ex) {
                // nop-op: noisy and redundant
            }
        }

        if (httpServer == null) {
            future.complete();
            return;
        }
        httpServer.close(future.completer());
    }

    private void startJolokia() {
        try {
            logger.info("Starting Jolokia agent on port: {}", applicationConfiguration.getJolokiaPort());
            Map<String, String> config = Maps.newHashMap();
            config.put("port", "" + applicationConfiguration.getJolokiaPort());
            config.put("debug", "" + applicationConfiguration.isJolokiaDebugEnabled());
            jolokiaServer = new JolokiaServer(new JolokiaServerConfig(config), true);
            jolokiaServer.start();
        } catch (Exception ex) {
            logger.warn("Failed to start Jolokia agent!", ex);
        }
    }

    private Router router() {
        Router router = Router.router(vertx);

        // assign the global error handler
        router.route().failureHandler(GlobalExceptionHandler::error);

        router.route().handler(CookieHandler.create());

        router.route().handler(FaviconHandler.create("webroot/images/favicon.ico"));

        router.route().handler(ResponseTimeHandler.create());

        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        router.route().handler(BodyHandler.create());

        router
                .route()
                .handler(
                        context -> {
                            if (!applicationConfiguration.isAuthenticationEnabled()) {
                Session session = context.session();
                                if (!WebServerUtils.isLoggedIn(
                                        session, applicationConfiguration.getCannedUserName())) {
                                    WebServerUtils.assignUserToSession(
                                            session, applicationConfiguration.getCannedUserName());
                                }
                            }

                            WebServerUtils.jsonContentType(context.response());

                            context.next();
                        });

        // static resources
        staticHandler(router);

        // dynamic resources
        dynamicPages(router);

        // reroute requests to the root url onwards to the entry page
        router
                .route("/")
                .handler(event -> event.reroute(WebServerUtils.withApplicationName("about.hbs")));

        // event bus
        router.route(WebServerUtils.withApplicationName("eventbus/*")).handler(eventBusHandler());

        // REST api
        for (RestResource restResource : restResources) {
            logger.info("Registering resource: {}", restResource.getClass().getSimpleName());
            restResource.configure(vertx, httpServer, router);
        }

        return logRoutes(router);
    }

    private SockJSHandler eventBusHandler() {
        SockJSHandler handler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions();
        // open access
        PermittedOptions permitted = new PermittedOptions();
        options.addOutboundPermitted(permitted);
        handler.bridge(options);
        return handler;
    }

    private void staticHandler(Router router) {
        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setCachingEnabled(applicationConfiguration.isViewStaticAssetsCacheEnabled());
        staticHandler.setIndexPage(WebServerUtils.withApplicationName("about.hbs"));
        router.route("/assets/*").handler(staticHandler);
    }

    private void dynamicPages(Router router) {
        HandlebarsTemplateEngine hbsEngine = new ClasspathAwareHandlebarsTemplateEngine();

        hbsEngine.setMaxCacheSize(applicationConfiguration.getViewTemplateCacheSize());

        TemplateHandler templateHandler = TemplateHandler.create(hbsEngine);

        if (applicationConfiguration.isAuthenticationEnabled()) {
            router.get(WebServerUtils.withApplicationName("dataset/*")).handler(this::fromSession);
            router.get(WebServerUtils.withApplicationName("management/*")).handler(this::fromSession);
        }

        router
                .getWithRegex(".+\\.hbs")
                .handler(
                        context -> {
                            final Session session = context.session();
                            WebServerUtils.assignUserToRoutingContext(context);
                            context.next();
                        });

        router.getWithRegex(".+\\.hbs").handler(templateHandler);
    }

    private void fromSession(RoutingContext routingContext) {
        String userName = WebServerUtils.getCurrentUserName(routingContext.session());
        if (userName == null) {
            throw new AccessDeniedException();
        }
        routingContext.next();
    }

    @SuppressWarnings("unchecked")
    private Router logRoutes(Router router) {
        try {
            for (Route route : router.getRoutes()) {
                // path is public but methods are not, we use reflection to make that accessible
                @SuppressWarnings("JavaReflectionMemberAccess")
                Field f = route.getClass().getDeclaredField("methods");
                f.setAccessible(true);
                Set<HttpMethod> methods = (Set<HttpMethod>) f.get(route);
                if (isNotBlank(route.getPath())) {
                    methods.forEach(httpMethod -> logger.info("Route: [{}] {}", httpMethod, route.getPath()));
                }
            }
        } catch (Exception ex) {
            logger.info("Could not list a route due to: {}!", ex.getMessage());
        }

        return router;
    }

    private HttpServerOptions createOptions() {
        Integer port = applicationConfiguration.getHttpPort();
        logger.info("Starting HTTP server on port: {}", port);

        HttpServerOptions options = new HttpServerOptions();

        options.setLogActivity(true);
        options.setHost("localhost");
        options.setPort(port);
        return options;
    }
}
