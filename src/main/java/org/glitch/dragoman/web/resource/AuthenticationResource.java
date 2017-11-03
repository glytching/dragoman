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
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.glitch.dragoman.authentication.AuthenticationDao;
import org.glitch.dragoman.web.exception.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.glitch.dragoman.web.WebServerUtils.*;

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
        router.post(withApplicationName("login")).blockingHandler(this::login);
        router.post(withApplicationName("logout")).blockingHandler(this::logout);
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
                assignUserToSession(routingContext.session(), login);
            } else {
                throw new InvalidCredentialsException();
            }
        } else {
            logger.info("Creating a new user for: {}", login);
            authenticationDao.createUser(login, password);
            assignUserToSession(routingContext.session(), login);
        }

        redirectToLandingPage(routingContext);
    }

    public void logout(RoutingContext routingContext) {
        removeUserFromSession(routingContext.session());

        redirectToLandingPage(routingContext);
    }

    private void redirectToLandingPage(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.setStatusCode(303);
        response.headers().add("Location", withApplicationName("about.hbs"));
        response.end();
    }
}