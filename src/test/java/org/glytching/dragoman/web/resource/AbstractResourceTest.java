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
package org.glytching.dragoman.web.resource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import org.glytching.dragoman.configuration.ApplicationConfiguration;
import org.glytching.dragoman.configuration.guice.AppModule;
import org.glytching.dragoman.http.HttpClient;
import org.glytching.dragoman.http.HttpResponse;
import org.glytching.dragoman.reader.Reader;
import org.glytching.dragoman.repository.router.RepositoryRouter;
import org.glytching.dragoman.util.SystemPropertyRule;
import org.glytching.dragoman.web.RestOverridesModule;
import org.glytching.dragoman.web.WebServerVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public abstract class AbstractResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceTest.class);

    @ClassRule
    public static final SystemPropertyRule systemPropertyRule = new SystemPropertyRule("env", "embedded");

    @Inject
    protected Vertx vertx;
    @Inject
    private DeploymentOptions deploymentOptions;
    @Inject
    private Reader reader;
    @Inject
    private Set<RestResource> restResources;
    @Inject
    private HttpClient httpClient;
    @Inject
    protected ViewTransformer viewTransformer;
    @Inject
    protected RepositoryRouter repositoryRouter;
    @Inject
    protected ApplicationConfiguration configuration;

    protected int port;

    @Before
    @SuppressWarnings("unchecked")
    public void start(TestContext context) {
        Injector injector = Guice.createInjector(Modules.override(new AppModule()).with(new RestOverridesModule()));
        injector.injectMembers(this);

        startHttpServer(context);
    }

    @After
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

    private void startHttpServer(TestContext context) {
        port = configuration.getHttpPort();
        logger.info("Starting embedded HTTP server on port: {}", port);

        vertx.deployVerticle(new WebServerVerticle(restResources, configuration), deploymentOptions, context
                .asyncAssertSuccess());

        logger.info("Started embedded HTTP server");
    }
}