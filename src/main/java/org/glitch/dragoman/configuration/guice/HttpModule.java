package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.http.HttpClient;
import org.glitch.dragoman.http.HttpClientAdapter;
import org.glitch.dragoman.http.HttpClientAdapterImpl;
import org.glitch.dragoman.http.okhttp.OkHttpClient;

public class HttpModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpClientAdapter.class).to(HttpClientAdapterImpl.class);
        bind(HttpClient.class).to(OkHttpClient.class);
    }
}