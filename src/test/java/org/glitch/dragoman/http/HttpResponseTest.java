package org.glitch.dragoman.http;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HttpResponseTest {

    private final String message = "aMessage";
    private final String url = "aUrl";
    private final HashMap<String, List<String>> headers = new HashMap<>();
    private final String payload = "aPayload";

    @Test
    public void canWrapASuccessfulResponse() {
        int statusCode = 200;

        HttpResponse httpResponse = new HttpResponse(statusCode, message, url, headers, payload);

        assertThat(httpResponse.getStatusCode(), is(statusCode));
        assertThat(httpResponse.getStatusMessage(), is(message));
        assertThat(httpResponse.getUrl(), is(url));
        assertThat(httpResponse.getHeaders(), is(headers));
        assertThat(httpResponse.getPayload(), is(payload));
        assertThat(httpResponse.isSuccessful(), is(true));
    }

    @Test
    public void canWrapAnUnsuccessfulResponse() {
        int statusCode = 404;

        HttpResponse httpResponse = new HttpResponse(statusCode, message, url, headers, payload);

        assertThat(httpResponse.getStatusCode(), is(statusCode));
        assertThat(httpResponse.getStatusMessage(), is(message));
        assertThat(httpResponse.getUrl(), is(url));
        assertThat(httpResponse.getHeaders(), is(headers));
        assertThat(httpResponse.getPayload(), is(payload));
        assertThat(httpResponse.isSuccessful(), is(false));
    }
}