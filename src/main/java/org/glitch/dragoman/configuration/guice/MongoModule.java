package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.MongoProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(MongoModule.class);

    @Override
    protected void configure() {
        bind(MongoProvider.class).to(MongoProviderImpl.class);
    }
}