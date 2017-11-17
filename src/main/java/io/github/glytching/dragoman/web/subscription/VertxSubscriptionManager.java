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
package io.github.glytching.dragoman.web.subscription;

import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.dataset.DatasetDao;
import io.github.glytching.dragoman.reader.DataEnvelope;
import io.github.glytching.dragoman.reader.Reader;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class VertxSubscriptionManager implements SubscriptionManager {
  private static final Logger logger = LoggerFactory.getLogger(VertxSubscriptionManager.class);

  private static final long DEFAULT_REFRESH_PERIOD = 15000L;

  private final Vertx vertx;
  private final DatasetDao datasetDao;
  private final Reader reader;
  private final AsOfFactory asOfFactory;
  private final Map<String, SubscriptionContext> subscriptions;
  private final WorkerExecutor executor;

  @Inject
  public VertxSubscriptionManager(
      Vertx vertx, DatasetDao datasetDao, Reader reader, AsOfFactory asOfFactory) {
    this.vertx = vertx;
    this.datasetDao = datasetDao;
    this.reader = reader;
    this.asOfFactory = asOfFactory;
    this.subscriptions = new ConcurrentHashMap<>();
    this.executor = vertx.createSharedWorkerExecutor("subscription-manager");
  }

  @Override
  public void start(
      Dataset dataset,
      Optional<Long> refreshPeriodInMillis,
      LocalDateTime startTime,
      String select,
      String where) {
    validateForSubscription(dataset);

    String subscriptionKey = dataset.getId();

    long refreshPeriod = refreshPeriodInMillis.orElse(DEFAULT_REFRESH_PERIOD);

    logger.info(
        "Creating a subscription for key: {} and period: {}", subscriptionKey, refreshPeriod);

    // use a blocking handler to ensure that the publication work does *not* occur on an event loop
    // thread
    long timerId =
        vertx.setPeriodic(
            refreshPeriod,
            aLong ->
                vertx.executeBlocking(
                    (Handler<Future<Void>>) future -> publishContent(dataset, select, where),
                    future -> {}));

    subscriptions.put(
        subscriptionKey,
        new SubscriptionContext(
            timerId,
            asOfFactory.create(
                dataset.getSubscriptionControlField(),
                dataset.getSubscriptionControlFieldPattern(),
                startTime)));
  }

  @Override
  public void stop(String subscriptionKey) {
    if (subscriptions.containsKey(subscriptionKey)) {
      logger.info("Cancelling subscription for: {}", subscriptionKey);
      boolean cancelled = vertx.cancelTimer(subscriptions.get(subscriptionKey).getTimerId());
      if (cancelled) {
        subscriptions.remove(subscriptionKey);
      } else {
        logger.warn("Failed to cancel subscription for: {}!", subscriptionKey);
      }
    } else {
      logger.info("Received stop for non existent subscriptionKey: {}", subscriptionKey);
    }
  }

  private void publishContent(Dataset dataset, String select, String where) {
    String subscriptionKey = dataset.getId();

    if (datasetDao.exists(dataset.getId())) {
      if (subscriptions.containsKey(subscriptionKey)) {
        logger.info("Publishing dataset content for: {}", subscriptionKey);
        SubscriptionContext subscriptionContext = subscriptions.get(subscriptionKey);
        Observable<DataEnvelope> read =
            reader.read(dataset, select, subscriptionContext.getAsOf().applyAsOf(where), "", -1);
        read.subscribe(
            dataEnvelope -> publishOne(subscriptionKey, dataset, dataEnvelope),
            throwable -> publishFailure(subscriptionKey, dataset, throwable),
            () -> publishCompletion(subscriptionKey, dataset));
      } else {
        logger.info(
            "Received a request to publish contents for subscriptionKey: {} but there is no such subscription present!",
            subscriptionKey);
      }
    } else {
      logger.info("Dataset: {} no longer exists, cancelling its subscription!", dataset);
      stop(subscriptionKey);
    }
  }

  private void publishOne(String subscriptionKey, Dataset dataset, DataEnvelope dataEnvelope) {
    logger.info("Publishing a subscription event for subscriptionKey: {}", subscriptionKey);
    publish(dataset, new SubscriptionStreamEvent<>(subscriptionKey, dataEnvelope.getPayload()));
  }

  private void publishFailure(String subscriptionKey, Dataset dataset, Throwable throwable) {
    logger.warn("Failed to read data for subscriptionKey: {}!", subscriptionKey, throwable);
    publish(
        dataset,
        new SubscriptionStreamFailedEvent(subscriptionKey, throwable.getMessage(), throwable));
  }

  private void publishCompletion(String subscriptionKey, Dataset dataset) {
    logger.info("Completed publication for subscriptionKey: {}", subscriptionKey);
    publish(dataset, new SubscriptionStreamCompletedEvent(subscriptionKey));
  }

  private void publish(Dataset dataset, Object event) {
    vertx.eventBus().publish(dataset.getId(), JsonObject.mapFrom(event));
  }

  private void validateForSubscription(Dataset dataset) {
    if (isBlank(dataset.getSubscriptionControlField())) {
      throw SubscriptionUnsupportedException.createUnsubscribable(dataset);
    }
    if (subscriptions.containsKey(dataset.getId())) {
      throw SubscriptionUnsupportedException.createConcurrentSubscription(dataset);
    }
  }
}
