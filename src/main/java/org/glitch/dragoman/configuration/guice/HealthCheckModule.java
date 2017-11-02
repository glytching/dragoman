package org.glitch.dragoman.configuration.guice;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.glitch.dragoman.store.mongo.health.IsMongoConnected;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.MongoProviderImpl;

public class HealthCheckModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<HealthCheck> multibinder = Multibinder.newSetBinder(binder(), HealthCheck.class);
        multibinder.addBinding().to(IsMongoConnected.class);
        bind(MongoProvider.class).to(MongoProviderImpl.class);
    }
}