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
package io.github.glytching.dragoman.web.resource;

import io.github.glytching.dragoman.authentication.AuthenticationDao;
import io.github.glytching.dragoman.web.WebServerUtils;
import io.github.glytching.dragoman.web.exception.InvalidCredentialsException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * A {@link RestResource} which exposes login/logout functionality.
 */
public class AuthenticationResource implements RestResource {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationResource.class);

    private final AuthenticationDao authenticationDao;

    @Inject
    public AuthenticationResource(AuthenticationDao authenticationDao) {
        this.authenticationDao = authenticationDao;
    }

    @Override
    public void configure(Vertx vertx, HttpServer httpServer, Router router) {
        router.post(WebServerUtils.withApplicationName("login")).blockingHandler(this::login);
        router.post(WebServerUtils.withApplicationName("logout")).blockingHandler(this::logout);
    }

    private void login(RoutingContext routingContext) {
        JsonObject bodyAsJson = routingContext.getBodyAsJson();
        final String login = bodyAsJson.getString("username");
        final String password = bodyAsJson.getString("password");

        logger.info("Attempting to login for user: {}", login);

        // if a user exists then
        //      test password match
        //      fail if unmatched
        // else
        //      create a new user
        // end
        if (authenticationDao.exists(login)) {
            if (authenticationDao.isValid(login, password)) {
                WebServerUtils.assignUserToSession(routingContext.session(), login);
            } else {
                throw new InvalidCredentialsException();
            }
        } else {
            logger.info("Creating a new user for: {}", login);
            authenticationDao.createUser(login, password);
            WebServerUtils.assignUserToSession(routingContext.session(), login);
        }

        redirectToLandingPage(routingContext);
    }

    public void logout(RoutingContext routingContext) {
        WebServerUtils.removeUserFromSession(routingContext.session());

        redirectToLandingPage(routingContext);
    }

    private void redirectToLandingPage(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.setStatusCode(303);
        response.headers().add("Location", WebServerUtils.withApplicationName("about.hbs"));
        response.end();
    }
}
