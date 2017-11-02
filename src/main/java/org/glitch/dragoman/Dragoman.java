package org.glitch.dragoman;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import org.glitch.dragoman.configuration.guice.AppModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME;

public class Dragoman {
    private static final Logger logger = LoggerFactory.getLogger(Dragoman.class);

    @Inject
    private Vertx vertx;
    @Inject
    private DeploymentOptions deploymentOptions;
    @Inject
    private MainVerticle mainVerticle;

    public static void main(String[] args) {
        // establish the bridge from vert.x JUL -> SLF4J -> Logback
        System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());

        new Dragoman().start();
    }

    private Dragoman() {
        Injector injector = Guice.createInjector(new AppModule());
        injector.injectMembers(this);
    }

    private void start() {
        vertx.deployVerticle(mainVerticle, deploymentOptions, stringAsyncResult ->
                logger.info("Started HTTP server: {}", stringAsyncResult.result()));
    }
}
