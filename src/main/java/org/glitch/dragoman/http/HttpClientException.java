package org.glitch.dragoman.http;

import static java.lang.String.format;

public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String url, Throwable ex) {
        super(format("Failed to read from: %s, caused by: %s", url, ex.getMessage()), ex);
    }
}