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
package io.github.glytching.dragoman.store.http;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static io.github.glytching.dragoman.util.NetworkUtils.getFreePort;
import static org.mockito.Mockito.mock;

public abstract class AbstractHttpTestCase {
  private static final Logger logger = LoggerFactory.getLogger(AbstractHttpTestCase.class);
  protected static int port;
  protected static HttpDataProvider httpDataProvider;
  private static Vertx vertx;

  @BeforeAll
  public static void start() {
    port = getFreePort();

    httpDataProvider = mock(HttpDataProvider.class);
    logger.info("Starting embedded HTTP server on port: {}", port);
    vertx = Vertx.vertx();
    DeploymentOptions options =
        new DeploymentOptions().setConfig(new JsonObject().put("http.port", port)).setInstances(1);

    CountDownLatch latch = new CountDownLatch(1);
    vertx.deployVerticle(
        new HttpServerSimulatorVerticle(httpDataProvider),
        options,
        result -> {
          logger.info("Started embedded HTTP server with result: {}", result);
          latch.countDown();
        });

    try {
      latch.await();
    } catch (InterruptedException e) {
      logger.warn("Failed to wait for the embedded HTTP server to start!");
    }
  }

  @AfterAll
  public static void stop() {
    logger.info("Stopping embedded HTTP server");
  }

  protected String getUrl() {
    return String.format("http://localhost:%s%s", port, HttpServerSimulatorVerticle.QUERY_ADDRESS);
  }
}
