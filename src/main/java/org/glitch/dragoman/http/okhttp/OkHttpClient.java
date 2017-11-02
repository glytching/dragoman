package org.glitch.dragoman.http.okhttp;

import com.google.common.annotations.VisibleForTesting;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.glitch.dragoman.http.HttpClient;
import org.glitch.dragoman.http.HttpClientException;
import org.glitch.dragoman.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.Callable;

public class OkHttpClient implements HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpClient.class);

    private final okhttp3.OkHttpClient httpClient;

    @Inject
    public OkHttpClient() {
        this.httpClient = new okhttp3.OkHttpClient();
    }

    @VisibleForTesting
    public OkHttpClient(okhttp3.OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpResponse get(String url) {

        logger.info("Get from: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        return execute(url, () -> handleResponse(url, httpClient.newCall(request).execute()));
    }

    @Override
    public HttpResponse delete(String url) {
        logger.info("Delete from: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        return execute(url, () -> handleResponse(url, httpClient.newCall(request).execute()));
    }

    @Override
    public HttpResponse post(String url, String json) {
        logger.info("Post to: {}", url);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (json != null) {
            requestBuilder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));
        }

        return execute(url, () -> handleResponse(url, httpClient.newCall(requestBuilder.build()).execute()));
    }

    @Override
    public HttpResponse put(String url, String json) {
        logger.info("Put to: {}", url);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (json != null) {
            requestBuilder.put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));
        }

        return execute(url, () -> handleResponse(url, httpClient.newCall(requestBuilder.build()).execute()));
    }

    private <T> T execute(String url, Callable<T> c) {
        try {
            return c.call();
        } catch (Exception ex) {
            throw new HttpClientException(url, ex);
        }
    }

    private HttpResponse handleResponse(String url, Response response) {
        try {
            return new HttpResponse(response.code(), response.message(), response.request().url().toString(),
                    response.headers().toMultimap(),
                    response.body().string());
        } catch (Exception ex) {
            throw new HttpClientException(url, ex);
        }
    }
}