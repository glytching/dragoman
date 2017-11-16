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

import io.github.glytching.junit.extension.system.SystemProperty;
import org.glytching.dragoman.http.HttpResponse;
import org.glytching.dragoman.store.mongo.health.IsMongoConnected;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SystemProperty(name = "env", value = "embedded")
public class ActuatorResourceTest extends AbstractResourceTest {

    @Test
    public void canPing() {
        HttpResponse response = read("ping");

        assertResponseBasics(response, 200, "text/plain");

        assertThat(response.getPayload(), is("pong"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canGetHealthCheck() {
        HttpResponse response = read("healthcheck");

        assertResponseBasics(response, 200, "application/json");

        Map<String, Object> transformed = viewTransformer.transform(Map.class, response.getPayload());

        // spot check
        assertThat(transformed, hasKey(IsMongoConnected.class.getSimpleName()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canGetMetrics() {
        HttpResponse response = read("metrics");

        assertResponseBasics(response, 200, "application/json");

        Map<String, Object> transformed = viewTransformer.transform(Map.class, response.getPayload());

        assertThat(transformed.size(), is(greaterThan(0)));
        // spot check a few
        assertThat(transformed, hasKey("post-requests"));
        assertThat(transformed, hasKey("get-requests"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canGetSpecificMetric() {
        HttpResponse response = read("metrics/get-requests");

        assertResponseBasics(response, 200, "application/json");

        Map<String, Object> transformed = viewTransformer.transform(Map.class, response.getPayload());

        // the number of elements in each metric
        assertThat(transformed.size(), is(19));
        // spot check a few
        assertThat(transformed, hasKey("type"));
        assertThat(transformed, hasKey("count"));
        assertThat(transformed, hasKey("meanRate"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canGetSpecificMetricByRegex() {
        HttpResponse response = read("metrics?regex=get");

        assertResponseBasics(response, 200, "application/json");

        Map<String, Object> transformed = viewTransformer.transform(Map.class, response.getPayload());

        assertThat(transformed.size(), is(1));
        assertThat(transformed, hasKey("get-requests"));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertResponseBasics(HttpResponse response, int statusCode, String contentType) {
        assertThat(response.getStatusCode(), is(statusCode));
        assertThat(response.getHeaders(), hasKey("content-type"));
        assertThat(response.getHeaders().get("content-type"), hasItem(contentType));
    }
}