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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.configuration.guice.DragomanModule;
import io.github.glytching.dragoman.http.HttpClient;
import io.github.glytching.dragoman.http.HttpResponse;
import io.github.glytching.dragoman.reader.Reader;
import io.github.glytching.dragoman.repository.router.RepositoryRouter;
import io.github.glytching.dragoman.web.RestOverridesModule;
import io.github.glytching.dragoman.web.WebServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;

public abstract class AbstractResourceTest {
  private static final Logger logger = LoggerFactory.getLogger(AbstractResourceTest.class);

  @Inject protected Vertx vertx;
  @Inject protected ViewTransformer viewTransformer;
  @Inject protected RepositoryRouter repositoryRouter;
  @Inject protected ApplicationConfiguration configuration;
  protected int port;
  @Inject private DeploymentOptions deploymentOptions;
  @Inject private Reader reader;
  @Inject private Set<RestResource> restResources;
  @Inject private HttpClient httpClient;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void start() {
    Injector injector =
        Guice.createInjector(
            Modules.override(new DragomanModule()).with(new RestOverridesModule()));
    injector.injectMembers(this);

    startHttpServer();
  }

  @AfterEach
  public void stop() {
    if (vertx != null) {
      logger.info("Stopping embedded HTTP server");
      vertx.close();
      logger.info("Stopped embedded HTTP server");
    }
  }

  protected String readSimple(String endpoint) {
    String url = getUrl(endpoint);
    return httpClient.get(url).getPayload();
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Object> readMap(String endpoint) {
    String response = readSimple(endpoint);

    return viewTransformer.transform(Map.class, response);
  }

  @SuppressWarnings("unchecked")
  protected List<Map<String, Object>> readList(String endpoint) {
    String url = getUrl(endpoint);
    String response = httpClient.get(url).getPayload();
    return viewTransformer.transform(List.class, response);
  }

  protected HttpResponse read(String endpoint) {
    String url = getUrl(endpoint);
    return httpClient.get(url);
  }

  protected HttpResponse post(String endpoint, String payload) {
    String url = getUrl(endpoint);
    return httpClient.post(url, payload);
  }

  protected HttpResponse put(String endpoint, String payload) {
    String url = getUrl(endpoint);
    return httpClient.put(url, payload);
  }

  protected HttpResponse delete(String endpoint) {
    String url = getUrl(endpoint);
    return httpClient.delete(url);
  }

  private String getUrl(String endpointAddress) {
    return format("http://localhost:%s/dragoman/%s", port, endpointAddress);
  }

  private void startHttpServer() {
    port = configuration.getHttpPort();
    logger.info("Starting embedded HTTP server on port: {}", port);
    CountDownLatch latch = new CountDownLatch(1);
    vertx.deployVerticle(
        new WebServerVerticle(restResources, configuration),
        deploymentOptions,
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
}
