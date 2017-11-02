package org.glitch.dragoman.web.resource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.configuration.guice.AppModule;
import org.glitch.dragoman.http.HttpClient;
import org.glitch.dragoman.http.HttpResponse;
import org.glitch.dragoman.reader.Reader;
import org.glitch.dragoman.repository.router.RepositoryRouter;
import org.glitch.dragoman.util.SystemPropertyRule;
import org.glitch.dragoman.web.RestOverridesModule;
import org.glitch.dragoman.web.WebServerVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public abstract class AbstractResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceTest.class);

    @ClassRule
    public static final SystemPropertyRule systemPropertyRule = new SystemPropertyRule("env", "embedded");

    @Inject
    protected Vertx vertx;
    @Inject
    private DeploymentOptions deploymentOptions;
    @Inject
    private Reader reader;
    @Inject
    private Set<RestResource> restResources;
    @Inject
    private HttpClient httpClient;
    @Inject
    protected ViewTransformer viewTransformer;
    @Inject
    protected RepositoryRouter repositoryRouter;
    @Inject
    protected ApplicationConfiguration configuration;

    protected int port;

    @Before
    @SuppressWarnings("unchecked")
    public void start(TestContext context) {
        Injector injector = Guice.createInjector(Modules.override(new AppModule()).with(new RestOverridesModule()));
        injector.injectMembers(this);

        startHttpServer(context);
    }

    @After
    public void stop() {
        if (vertx != null) {
            logger.info("Stopping embedded HTTP server");
            vertx.close();
            logger.info("Stopped embedded HTTP server");
        }
    }

    protected String readSimple(String endpoint) {
        String url = getUrl(endpoint);
        return httpClient.get(url).getPayload();
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> readMap(String endpoint) {
        String response = readSimple(endpoint);

        return viewTransformer.transform(Map.class, response);
    }

    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> readList(String endpoint) {
        String url = getUrl(endpoint);
        String response = httpClient.get(url).getPayload();
        return viewTransformer.transform(List.class, response);
    }

    protected HttpResponse read(String endpoint) {
        String url = getUrl(endpoint);
        return httpClient.get(url);
    }

    protected HttpResponse post(String endpoint, String payload) {
        String url = getUrl(endpoint);
        return httpClient.post(url, payload);
    }

    protected HttpResponse put(String endpoint, String payload) {
        String url = getUrl(endpoint);
        return httpClient.put(url, payload);
    }

    protected HttpResponse delete(String endpoint) {
        String url = getUrl(endpoint);
        return httpClient.delete(url);
    }

    private String getUrl(String endpointAddress) {
        return format("http://localhost:%s/dragoman/%s", port, endpointAddress);
    }

    private void startHttpServer(TestContext context) {
        port = configuration.getHttpPort();
        logger.info("Starting embedded HTTP server on port: {}", port);

        vertx.deployVerticle(new WebServerVerticle(restResources, configuration), deploymentOptions, context
                .asyncAssertSuccess());

        logger.info("Started embedded HTTP server");
    }
}