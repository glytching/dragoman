package org.glitch.dragoman.http;

import org.glitch.dragoman.store.http.repository.ResponsePostProcessor;
import org.glitch.dragoman.transform.JsonTransformer;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class HttpClientAdapterImpl implements HttpClientAdapter {

    private final HttpClient httpClient;
    private final JsonTransformer jsonTransformer;

    @Inject
    public HttpClientAdapterImpl(HttpClient httpClient, JsonTransformer jsonTransformer) {
        this.httpClient = httpClient;
        this.jsonTransformer = jsonTransformer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Map<String, Object>> read(String url, ResponsePostProcessor responsePostProcessor) {
        HttpResponse response = httpClient.get(url);

        if (!response.isSuccessful()) {
            throw new HttpClientException(format("Failed to read response from: %s, got: %s", url, response));
        }

        String content = responsePostProcessor.postProcess(response.getPayload());

        List<Map<String, Object>> transformed = jsonTransformer.transform(List.class, content);
        return Observable.from(transformed);
    }
}