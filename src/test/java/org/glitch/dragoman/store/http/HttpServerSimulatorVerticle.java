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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.glitch.dragoman.web.WebServerUtils.jsonContentType;

public class HttpServerSimulatorVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerSimulatorVerticle.class);

    public static final String QUERY_ADDRESS = "/simulator";

    private final HttpDataProvider httpDataProvider;
    private final ObjectMapper objectMapper;

    public HttpServerSimulatorVerticle(HttpDataProvider httpDataProvider) {
        this.httpDataProvider = httpDataProvider;
        this.objectMapper = createObjectMapper();
    }

    @Override
    public void stop() throws Exception {
        vertx.close();
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.get(QUERY_ADDRESS).blockingHandler(this::getAll);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080));
    }

    private void getAll(RoutingContext routingContext) {
        jsonContentType(routingContext.response())
                .end(serialise(httpDataProvider.getAll()));
    }

    private String serialise(List<Map<String, Object>> value) {
        logger.info("Serialising HTTP server simulator response");

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to serialise response!", ex);
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        return objectMapper;
    }
}
