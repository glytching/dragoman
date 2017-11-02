package org.glitch.dragoman.http;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;
    private final String statusMessage;
    private final String url;
    private final Map<String, List<String>> headers;
    private final String payload;

    public HttpResponse(int statusCode, String statusMessage, String url, Map<String, List<String>> headers,
                        String payload) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.url = url;
        this.headers = headers;
        this.payload = payload;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
