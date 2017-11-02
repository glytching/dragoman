package org.glitch.dragoman.store.mongo.repository;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.mockito.Mockito;

public class MongoOverrideModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MongoProvider.class).toInstance(Mockito.mock(MongoProvider.class));
    }
}