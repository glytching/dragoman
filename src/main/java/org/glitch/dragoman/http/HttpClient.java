package org.glitch.dragoman.http;

public interface HttpClient {

    HttpResponse get(String url);

    HttpResponse delete(String url);

    HttpResponse post(String url, String json);

    HttpResponse put(String url, String json);
}