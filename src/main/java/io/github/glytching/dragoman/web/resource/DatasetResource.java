/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.glytching.dragoman.web.resource;

import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.dataset.DatasetDao;
import io.github.glytching.dragoman.reader.DataEnvelope;
import io.github.glytching.dragoman.reader.Reader;
import io.github.glytching.dragoman.web.GlobalExceptionHandler;
import io.github.glytching.dragoman.web.exception.AccessDeniedException;
import io.github.glytching.dragoman.web.exception.InvalidRequestException;
import io.github.glytching.dragoman.web.subscription.SubscriptionManager;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.glytching.dragoman.web.WebServerUtils.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * A {@link RestResource} which eposes all the dataset management endpoints.
 */
public class DatasetResource implements RestResource {
    private static final Logger logger = LoggerFactory.getLogger(DatasetResource.class);

    private final DatasetDao datasetDao;
    private final Reader reader;
    private final ViewTransformer viewTransformer;
    private final SubscriptionManager subscriptionManager;
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    public DatasetResource(
            DatasetDao datasetDao,
            Reader reader,
            ViewTransformer viewTransformer,
            SubscriptionManager subscriptionManager,
            ApplicationConfiguration applicationConfiguration) {
        this.datasetDao = datasetDao;
        this.reader = reader;
        this.viewTransformer = viewTransformer;
        this.subscriptionManager = subscriptionManager;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void configure(Vertx vertx, HttpServer httpServer, Router router) {
        //
        // getAll returns an observable to which we subscribe and write the observed stream into the
        // HTTP response so
        // this can use a non blocking handler
        //
        router.get(withApplicationName("datasets")).handler(this::getAll);

        //
        // the remaining endpoints may be blocking (e.g. any reads from HTTP sources) or are simple,
        // quick calls to a
        // Dao and the additional illegibility of exposing Observables is deemed not worth the bother so
        // these all use
        // blocking handlers
        //

        router.get(withApplicationName("dataset/:id")).blockingHandler(this::getOne);

        router.post(withApplicationName("dataset")).blockingHandler(this::createDataset);

        router.put(withApplicationName("dataset")).blockingHandler(this::createDataset);

        router.delete(withApplicationName("dataset/:id")).blockingHandler(this::deleteDataset);

        router.get(withApplicationName("dataset/:id/sample")).blockingHandler(this::getSample);

        router.get(withApplicationName("dataset/:id/content")).blockingHandler(this::getContent);

        router
                .delete(withApplicationName("dataset/:id/content"))
                .blockingHandler(this::stopSubscription);
    }

    private void stopSubscription(RoutingContext routingContext) {
        String id = validateNotEmpty(routingContext, "id");

        subscriptionManager.stop(id);

        routingContext.response().end();
    }

    private void getContent(RoutingContext routingContext) {
        Dataset dataset = getDataset(routingContext);

        String select = routingContext.request().getParam("select");
        String where = routingContext.request().getParam("where");
        String orderBy = routingContext.request().getParam("orderBy");

        String subscriptionFlag = routingContext.request().getParam("subscribe");
        if (isNotBlank(subscriptionFlag)
                && !(subscriptionFlag.equalsIgnoreCase("true")
                || subscriptionFlag.equalsIgnoreCase("false"))) {
            throw InvalidRequestException.invalidValue("subscribe", subscriptionFlag, "true|false");
        }

        logger.info("Getting content for dataset: {}", dataset);

        if (Boolean.valueOf(subscriptionFlag)) {
            Optional<Long> refreshPeriod = Optional.empty();
            if (routingContext.request().params().contains("subscriptionInterval")) {
                refreshPeriod =
                        Optional.of(Long.valueOf(routingContext.request().getParam("subscriptionInterval")));
            }

            subscriptionManager.start(
                    dataset, refreshPeriod, LocalDateTime.now(ZoneId.of("UTC")), select, where);
        }

        writeDatasetContents(routingContext, reader.read(dataset, select, where, orderBy, -1));
    }

    private void getSample(RoutingContext routingContext) {
        Dataset dataset = getDataset(routingContext);

        logger.info("Sampling dataset: {}", dataset);

        writeDatasetContents(routingContext, reader.read(dataset, "", "", "", 10));
    }

    private void deleteDataset(RoutingContext routingContext) {
        String id = validateNotEmpty(routingContext, "id");

        logger.info("Deleting dataset for id: {}", id);

        long deleteCount = datasetDao.delete(id);

        logger.info("Delete count: {} for id: {}", deleteCount, id);

        routingContext.response().end();
    }

    private void createDataset(RoutingContext routingContext) {
        String user = getUserId(routingContext);

        JsonObject body = routingContext.getBodyAsJson();
        body.put("owner", user);

        Dataset dataset = datasetDao.write(viewTransformer.transform(Dataset.class, body.encode()));

        routingContext.response().end(viewTransformer.transform(dataset));
    }

    private void getOne(RoutingContext routingContext) {
        Dataset dataset = getDataset(routingContext);

        routingContext.response().end(viewTransformer.transform(dataset));
    }

    private void getAll(RoutingContext routingContext) {
        String user = getUserId(routingContext);

        logger.info("Getting all datasets for user: {}", user);

        Observable<Dataset> all = datasetDao.getAll(user);
        writeDatasets(routingContext, all);
    }

    private Dataset getDataset(RoutingContext routingContext) {
        String id = validateNotEmpty(routingContext, "id");

        logger.info("Getting dataset for id: {}", id);

        return datasetDao.get(id);
    }

    private void writeDatasetContents(
            RoutingContext routingContext, Observable<DataEnvelope> datasetContents) {
        HttpServerResponse httpServerResponse =
                jsonContentType(routingContext.response()).setChunked(true);

        final AtomicBoolean isFirst = new AtomicBoolean(true);
        datasetContents.subscribe(
                (DataEnvelope dataEnvelope) -> {
                    if (!isFirst.get()) {
                        httpServerResponse.write(",");
                    } else {
                        isFirst.set(false);
                        httpServerResponse.write("[");
                    }
                    httpServerResponse.write(new JsonObject(dataEnvelope.getPayload()).encodePrettily());
                },
                throwable -> GlobalExceptionHandler.error(routingContext, throwable),
                () -> httpServerResponse.end("]"));
    }

    private void writeDatasets(RoutingContext routingContext, Observable<Dataset> datasets) {
        HttpServerResponse httpServerResponse =
                jsonContentType(routingContext.response()).setChunked(true);

        final AtomicBoolean isFirst = new AtomicBoolean(true);
        datasets.subscribe(
                dataset -> {
                    if (!isFirst.get()) {
                        httpServerResponse.write(",");
                    } else {
                        isFirst.set(false);
                        httpServerResponse.write("[");
                    }
                    httpServerResponse.write(viewTransformer.transform(dataset));
                },
                throwable -> GlobalExceptionHandler.error(routingContext, throwable),
                () -> httpServerResponse.end("]"));
    }

    private String getUserId(RoutingContext routingContext) {
        if (applicationConfiguration.isAuthenticationEnabled()) {
            String user = getCurrentUserName(routingContext.session());
            if (isBlank(user)) {
                // this is unexpected!
                throw new AccessDeniedException();
            }
            return user;
        } else {
            return applicationConfiguration.getCannedUserName();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private String validateNotEmpty(RoutingContext routingContext, String parameterName) {
        String parameterValue = routingContext.request().getParam(parameterName);

        if (isBlank(parameterValue)) {
            throw InvalidRequestException.missingParameter(parameterName);
        }

        return parameterValue;
    }
}
