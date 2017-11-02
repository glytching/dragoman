package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.glitch.dragoman.web.resource.*;
import org.glitch.dragoman.web.subscription.SubscriptionManager;
import org.glitch.dragoman.web.subscription.VertxSubscriptionManager;

public class RestModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<RestResource> multibinder = Multibinder.newSetBinder(binder(), RestResource.class);
        multibinder.addBinding().to(PingResource.class);
        multibinder.addBinding().to(MetricsResource.class);
        multibinder.addBinding().to(HealthCheckResource.class);
        multibinder.addBinding().to(DatasetResource.class);
        multibinder.addBinding().to(AuthenticationResource.class);

        bind(SubscriptionManager.class).to(VertxSubscriptionManager.class);
    }
}