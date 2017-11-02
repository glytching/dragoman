package org.glitch.dragoman.http;

import org.glitch.dragoman.store.http.repository.ResponsePostProcessor;
import rx.Observable;

import java.util.Map;

/**
 * Defines the responsibilties for a facade which sits between a raw {@link HttpClient} and a
 * {@link org.glitch.dragoman.store.http.repository.HttpRepository}. The former returns a string reponse (JSON, for
 * example) whereas the latter operates on an {@code Observable<Map<String, Object>>}. This cannot be part of the
 * {@link org.glitch.dragoman.repository.Repository} hierarchy (e.g. a decorating form of the
 * {@link org.glitch.dragoman.store.http.repository.HttpRepository}) because it knows nothing about projections,
 * predicates etc instead it just knows about a URL.
 */
public interface HttpClientAdapter {

    Observable<Map<String, Object>> read(String url, ResponsePostProcessor responsePostProcessor);
}