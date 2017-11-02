package org.glitch.dragoman.web.resource;

import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.glitch.dragoman.http.HttpResponse;
import org.glitch.dragoman.store.mongo.health.IsMongoConnected;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(VertxUnitRunner.class)
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