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
package org.glitch.dragoman.store.http;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.glitch.dragoman.store.http.HttpServerSimulatorVerticle.QUERY_ADDRESS;
import static org.glitch.dragoman.util.NetworkUtils.getFreePort;
import static org.mockito.Mockito.mock;

@RunWith(VertxUnitRunner.class)
public abstract class AbstractHttpTestCase {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpTestCase.class);
    private static Vertx vertx;

    protected static int port;
    protected static HttpDataProvider httpDataProvider;

    @BeforeClass
    public static void start(TestContext context) {
        port = getFreePort();

        httpDataProvider = mock(HttpDataProvider.class);
        logger.info("Starting embedded HTTP server on port: {}", port);
        vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port))
                .setInstances(1);
        vertx.deployVerticle(new HttpServerSimulatorVerticle(httpDataProvider), options, context.asyncAssertSuccess());
        logger.info("Started embedded HTTP server");
    }

    @AfterClass
    public static void stop(TestContext context) {
        logger.info("Stopping embedded HTTP server");
        vertx.close(context.asyncAssertSuccess());
        logger.info("Stopped embedded HTTP server");
    }

    protected String getUrl() {
        return format("http://localhost:%s%s", port, QUERY_ADDRESS);
    }
}