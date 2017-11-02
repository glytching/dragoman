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
package org.glitch.dragoman.web.subscription;

import com.google.common.collect.Lists;
import com.jayway.awaitility.Awaitility;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.dataset.DatasetDao;
import org.glitch.dragoman.reader.DataEnvelope;
import org.glitch.dragoman.reader.Reader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.glitch.dragoman.util.TestFixture.aPersistedDataset;
import static org.glitch.dragoman.util.TestFixture.anyDataEnvelope;
import static org.glitch.dragoman.web.subscription.SubscriptionEvent.SubscriptionEventType;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VertxSubscriptionManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(VertxSubscriptionManagerTest.class);

    @Mock
    private Reader reader;
    @Mock
    private DatasetDao datasetDao;
    @Mock
    private AsOfFactory asOfFactory;

    private Vertx vertx;
    private String select;
    private String where;
    private Dataset dataset;
    private AsOf asOf;

    private VertxSubscriptionManager subscriptionManager;

    @Before
    public void prepare() {
        vertx = Vertx.vertx();

        dataset = aPersistedDataset();
        select = "select";
        where = "where";

        // asOf is tested elsewhere, from the perspective of this test case it is simply an external collaborator with
        // the same behaviour for every test in this test case
        asOf = mock(AsOf.class);
        when(asOf.applyAsOf(where)).thenReturn(where);
        when(asOfFactory.create(eq(dataset.getSubscriptionControlField()),
                eq(dataset.getSubscriptionControlFieldPattern()), any(LocalDateTime.class))).thenReturn(asOf);

        subscriptionManager = new VertxSubscriptionManager(vertx, datasetDao, reader, asOfFactory);
    }

    @Test
    public void canSubscribe() {
        when(datasetDao.exists(dataset.getId())).thenReturn(true);

        DataEnvelope one = anyDataEnvelope();
        DataEnvelope two = anyDataEnvelope();
        DataEnvelope three = anyDataEnvelope();
        //noinspection unchecked
        when(reader.read(dataset, select, asOf.applyAsOf(where), "", -1)).thenReturn(
                // return something
                Observable.just(one, two),
                // then nothing
                Observable.empty(),
                // then something again
                Observable.just(three)
        );

        Subscriber subscriber = new Subscriber(dataset.getId());

        long subscriptionInterval = 100L;
        subscriptionManager.start(dataset, Optional.of(subscriptionInterval), LocalDateTime.now(), select, where);

        // we're expecting at least three publications so let's wait for (subscriptionInterval * 3) + _some_padding_
        Awaitility.await().atMost(((subscriptionInterval * 3) + 500), TimeUnit.MILLISECONDS)
                .until(() -> subscriber.isCompleted(3));

        List<JsonObject> consumed = subscriber.getPayloads();
        assertThat(consumed, hasItem(new JsonObject(one.getPayload())));
        assertThat(consumed, hasItem(new JsonObject(two.getPayload())));
        assertThat(consumed, hasItem(new JsonObject(three.getPayload())));
    }

    @Test
    public void willGetNothingIfTheReaderFails() {
        when(datasetDao.exists(dataset.getId())).thenReturn(true);

        Subscriber subscriber = new Subscriber(dataset.getId());

        when(reader.read(dataset, select, asOf.applyAsOf(where), "", -1)).thenReturn(
                Observable.error(new RuntimeException("boom!"))
        );

        long subscriptionInterval = 50L;
        subscriptionManager.start(dataset, Optional.of(subscriptionInterval), LocalDateTime.now(), select, where);

        // have to wait for the async call to came through ...
        Awaitility.await().atMost((subscriptionInterval + 500), TimeUnit.MILLISECONDS)
                .until(subscriber::isFailed);

        assertThat(subscriber.getPayloads().isEmpty(), is(true));
    }

    @Test
    public void canSubscribeAndThenCancel() {
        when(datasetDao.exists(dataset.getId())).thenReturn(true);

        DataEnvelope one = anyDataEnvelope();
        when(reader.read(dataset, select, asOf.applyAsOf(where), "", -1)).thenReturn(
                // return something
                Observable.just(one)
        );

        Subscriber subscriber = new Subscriber(dataset.getId(), true);

        long subscriptionInterval = 250L;
        subscriptionManager.start(dataset, Optional.of(subscriptionInterval), LocalDateTime.now(), select, where);

        // we're expecting one publication so let's wait for (subscriptionInterval + _some_padding_)
        Awaitility.await().atMost(((subscriptionInterval + 500)), TimeUnit.MILLISECONDS)
                .until(() -> subscriber.isCompleted(1));

        assertThat(subscriber.getPayloads(), hasItem(new JsonObject(one.getPayload())));

        // should only have been one call to the reader since we cancelled as soon as we got a response
        verify(reader, times(1)).read(any(Dataset.class), anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    public void cannotSubscribeIfTheDatasetDoesNotExist() {
        when(datasetDao.exists(dataset.getId())).thenReturn(false);

        subscriptionManager.start(dataset, Optional.of(10L), LocalDateTime.now(), select, where);

        verify(reader, never()).read(any(Dataset.class), anyString(), anyString(), anyString(), anyInt());
    }

    @Test(expected = SubscriptionUnsupportedException.class)
    public void cannotSubscribeIfTheDatasetDoesNotSupportSubscriptions() {
        dataset.setSubscriptionControlField(null);

        subscriptionManager.start(dataset, Optional.of(10L), LocalDateTime.now(), select, where);
    }

    private class Subscriber {

        private int completedEventCount;
        private int failedEventCount;
        private final List<JsonObject> payloads;

        private Subscriber(String subscriptionKey) {
            this(subscriptionKey, false);
        }

        private Subscriber(String subscriptionKey, boolean cancelOnReceipt) {
            this.payloads = Lists.newArrayList();

            vertx.eventBus().consumer(subscriptionKey, (Handler<Message<JsonObject>>) event -> {
                logger.info("Received pushed content: {}", event.body().toString());

                if (event.body().containsKey("eventType")) {
                    SubscriptionEventType eventType =
                            SubscriptionEventType.valueOf(event.body().getString("eventType"));
                    if (eventType == SubscriptionEventType.STREAM_FAILED_EVENT) {
                        failedEventCount++;
                    } else if (eventType == SubscriptionEventType.STREAM_COMPLETED_EVENT) {
                        completedEventCount++;
                    } else {
                        if (cancelOnReceipt) {
                            logger.info("Stopping subscriber: {}", subscriptionKey);
                            subscriptionManager.stop(subscriptionKey);
                        }
                        // add it to the consumer stub
                        payloads.add((JsonObject) event.body().getMap().get("payload"));
                    }
                } else {
                    logger.warn("The subscription event does not contain an event type!");
                    completedEventCount++;
                }
            });
        }

        public List<JsonObject> getPayloads() {
            return payloads;
        }

        private boolean isCompleted(int expectedSubscriptionCount) {
            // the use of >= looks a bit odd, shouldn't we be certain about how many distinct subscription events we
            // receive? yes ... but there may be race conditions when using very small subscription intervals (as we do
            // in this test case) because between receiving an event and cancelling the subscription the next publication
            // may have already kicked in
            return this.completedEventCount >= expectedSubscriptionCount;
        }

        private boolean isFailed() {
            return this.failedEventCount > 0;
        }
    }
}