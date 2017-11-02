package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import org.glitch.dragoman.configuration.ApplicationConfiguration;

import javax.inject.Singleton;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ConfigurationModule());
        install(new MongoModule());
        install(new DatasetModule());
        install(new CannedDatasetsModule());
        install(new AuthenticationModule());
        install(new ReaderModule());
        install(new RepositoryModule());
        install(new HttpModule());
        install(new WebModule());
        install(new HealthCheckModule());
        install(new RestModule());
    }

    @Provides
    @Singleton
    public Vertx provideVertx(ApplicationConfiguration applicationConfiguration) {
        VertxOptions vertxOptions = new VertxOptions()
                .setMaxEventLoopExecuteTime(applicationConfiguration.getMaxEventLoopExecutionTime())
                .setWarningExceptionTime(20L * 1000 * 1000000)
                .setMaxWorkerExecuteTime(applicationConfiguration.getMaxWorkerExecutionTime())
                .setWorkerPoolSize(applicationConfiguration.getWorkerPoolSize());

        if (applicationConfiguration.isMetricsEnabled()) {
            // see https://github.com/vert-x3/vertx-dropwizard-metrics/blob/master/src/main/asciidoc/java/index.adoc#jmx
            vertxOptions.setMetricsOptions(
                    new DropwizardMetricsOptions()
                            .setEnabled(true)
                            .setJmxEnabled(true)
                            .setRegistryName("dragoman-registry")
                            .setJmxDomain("dragoman")
            );
        }

        return Vertx.vertx(vertxOptions);
    }

    @Provides
    @Singleton
    public DeploymentOptions provideDeploymentOptions(ApplicationConfiguration applicationConfiguration) {
        // Vert.x is a non-blocking event-loop based framework; it does not follow the approach of 1 connection -> 1 thread
        // instead since each request is handled in a request-calback-response fashion the event loop is (or should be)
        // ~immediately available to handle the next request so want to accept the default since this indicates
        // conformance with the Vert.x approach or to put it another way we want non conformance to be made obvious so
        // that we can fix it rather than for it to be hidden behind tweaked thread pools
        // Note: since Guice is creating the verticles for us we cannot set instances > 1, this _may_ be
        // reconsidered later ...
        return new DeploymentOptions()
                .setWorkerPoolName("vertx-worker")
                .setInstances(1);
    }
}