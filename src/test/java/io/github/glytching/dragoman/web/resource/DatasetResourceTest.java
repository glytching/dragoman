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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.awaitility.Awaitility;
import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.dataset.DatasetDao;
import io.github.glytching.dragoman.http.HttpResponse;
import io.github.glytching.dragoman.reader.DataEnvelope;
import io.github.glytching.dragoman.reader.Reader;
import io.github.glytching.dragoman.web.WebServerUtils;
import io.github.glytching.dragoman.web.subscription.SubscriptionEvent;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.github.glytching.junit.extension.system.SystemProperty;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.glytching.dragoman.util.TestFixture.aPersistedDataset;
import static io.github.glytching.dragoman.util.TestFixture.anyDataEnvelope;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
@SystemProperty(name = "env", value = "embedded")
public class DatasetResourceTest extends AbstractResourceTest {
  private static final Logger logger = LoggerFactory.getLogger(DatasetResourceTest.class);

  @Inject private DatasetDao datasetDao;
  @Inject private Reader reader;
  @Inject private Vertx vertx;
  @Inject private ApplicationConfiguration applicationConfiguration;

  @Test
  public void canGetAllDatasets(@Random Dataset one, @Random Dataset two) throws IOException {
    when(datasetDao.getAll(anyString())).thenReturn(Observable.just(one, two));

    HttpResponse response = read("datasets");
    assertThat(response.getStatusCode(), is(HttpResponseStatus.OK.code()));
    assertThat(response.getStatusMessage(), is(HttpResponseStatus.OK.reasonPhrase()));

    // cannot use the injected converter here because we are using a jackson specific TypeReference
    List<Dataset> datasets =
        new ObjectMapper().readValue(response.getPayload(), new TypeReference<List<Dataset>>() {});
    assertThat(datasets, hasSize(2));
    assertThat(datasets, hasItem(one));
    assertThat(datasets, hasItem(two));
  }

  @Test
  public void canHandleFailureForGetAllDatasets() {
    Throwable exception = new RuntimeException("boom!");

    when(datasetDao.getAll(anyString())).thenReturn(Observable.error(exception));

    String endpoint = "datasets";
    HttpResponse response = read(endpoint);
    assertThat(response.getStatusCode(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
    assertThat(
        response.getStatusMessage(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), exception.getMessage(), endpoint, INTERNAL_SERVER_ERROR);
  }

  @Test
  public void canGetDataset() {
    Dataset dataset = aPersistedDataset();

    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    HttpResponse response = read("dataset/" + dataset.getId());
    assertThat(response.getStatusCode(), is(HttpResponseStatus.OK.code()));
    assertThat(response.getStatusMessage(), is(HttpResponseStatus.OK.reasonPhrase()));

    Dataset actual = viewTransformer.transform(Dataset.class, response.getPayload());
    assertThat(actual, is(dataset));
  }

  @Test
  public void canHandleFailureForGetDataset() {
    String id = "aDatasetId";

    Exception exception = new RuntimeException("boom!");

    when(datasetDao.get(id)).thenThrow(exception);

    String endpoint = "dataset/" + id;
    HttpResponse response = read(endpoint);

    assertThat(response.getStatusCode(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
    assertThat(
        response.getStatusMessage(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), exception.getMessage(), endpoint, INTERNAL_SERVER_ERROR);
  }

  @Test
  public void canCreateDataset(@Random Dataset dataset) {
    String name = "aName";
    String source = "aSource";
    String subscriptionControlField = "aSubscriptionControlField";
    String subscriptionControlFieldPattern = "aSubscriptionControlFieldPattern";

    when(datasetDao.write(any(Dataset.class))).thenReturn(dataset);

    String payload =
        "{ \"name\": \""
            + name
            + "\", \"source\": \""
            + source
            + "\", "
            + "\"subscriptionControlField\": \""
            + subscriptionControlField
            + "\", "
            + "\"subscriptionControlFieldPattern\": \""
            + subscriptionControlFieldPattern
            + "\" }";

    HttpResponse response = post("dataset", payload);
    assertThat(response.getStatusCode(), is(HttpResponseStatus.OK.code()));
    assertThat(response.getStatusMessage(), is(HttpResponseStatus.OK.reasonPhrase()));

    Dataset actual = viewTransformer.transform(Dataset.class, response.getPayload());
    assertThat(actual, is(dataset));

    ArgumentCaptor<Dataset> datasetCaptor = ArgumentCaptor.forClass(Dataset.class);
    verify(datasetDao).write(datasetCaptor.capture());

    Dataset submittedDataset = datasetCaptor.getValue();
    assertThat(submittedDataset.getName(), is(name));
    assertThat(submittedDataset.getOwner(), is(applicationConfiguration.getCannedUserName()));
    assertThat(submittedDataset.getSource(), is(source));
    assertThat(submittedDataset.getSubscriptionControlField(), is(subscriptionControlField));
    assertThat(
        submittedDataset.getSubscriptionControlFieldPattern(), is(subscriptionControlFieldPattern));
  }

  @Test
  public void canHandleFailureWhenWritingADataset() {
    String name = "aName";
    String source = "aSource";

    Exception exception = new RuntimeException("boom!");
    when(datasetDao.write(any(Dataset.class))).thenThrow(exception);

    String payload = "{ \"name\": \"" + name + "\", \"source\": \"" + source + "\" }";

    String endpoint = "dataset";
    HttpResponse response = post(endpoint, payload);
    assertThat(response.getStatusCode(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
    assertThat(
        response.getStatusMessage(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), exception.getMessage(), endpoint, INTERNAL_SERVER_ERROR);
  }

  @Test
  public void dodgyBodyWillBeRejectedAsABadRequestWhenAttemptingToCreateDataset() {
    String endpoint = "dataset";
    HttpResponse response = post(endpoint, "{ \"foo\": \"whatever\" }");
    assertThat(response.getStatusCode(), is(HttpResponseStatus.BAD_REQUEST.code()));
    assertThat(response.getStatusMessage(), is(HttpResponseStatus.BAD_REQUEST.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), "Failed to deserialise request body", endpoint, BAD_REQUEST);

    verify(datasetDao, never()).write(any(Dataset.class));
  }

  @Test
  public void canUpdateDataset(@Random Dataset dataset) {
    String name = "aName";
    String source = "aSource";

    when(datasetDao.write(any(Dataset.class))).thenReturn(dataset);

    String payload = "{ \"name\": \"" + name + "\", \"source\": \"" + source + "\" }";

    HttpResponse response = put("dataset", payload);
    assertThat(response.getStatusCode(), is(OK.code()));
    assertThat(response.getStatusMessage(), is(OK.reasonPhrase()));

    Dataset actual = viewTransformer.transform(Dataset.class, response.getPayload());
    assertThat(actual, is(dataset));

    ArgumentCaptor<Dataset> datasetCaptor = ArgumentCaptor.forClass(Dataset.class);
    verify(datasetDao).write(datasetCaptor.capture());

    Dataset submittedDataset = datasetCaptor.getValue();
    assertThat(submittedDataset.getName(), is(name));
    assertThat(submittedDataset.getSource(), is(source));
  }

  @Test
  public void dodgyBodyWillBeRejectedAsABadRequestWhenAttemptingToUpdateDataset() {
    String endpoint = "dataset";
    HttpResponse response = put(endpoint, "{ \"foo\": \"whatever\" }");
    assertThat(response.getStatusCode(), is(HttpResponseStatus.BAD_REQUEST.code()));
    assertThat(response.getStatusMessage(), is(HttpResponseStatus.BAD_REQUEST.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), "Failed to deserialise request body", endpoint, BAD_REQUEST);

    verify(datasetDao, never()).write(any(Dataset.class));
  }

  @Test
  public void canDeleteDataset() {
    String id = "aDatasetId";

    HttpResponse response = delete("dataset/" + id);
    assertThat(response.getStatusCode(), is(OK.code()));
    assertThat(response.getStatusMessage(), is(OK.reasonPhrase()));

    assertThat(response.getPayload(), is(""));
    verify(datasetDao).delete(id);
  }

  @Test
  public void canHandleFailureWhenDeletingADataset() {
    String id = "aDatasetId";

    Exception exception = new RuntimeException("boom!");
    when(datasetDao.delete(id)).thenThrow(exception);

    String endpoint = "dataset/" + id;
    HttpResponse response = delete(endpoint);
    assertThat(response.getStatusCode(), is(INTERNAL_SERVER_ERROR.code()));
    assertThat(response.getStatusMessage(), is(INTERNAL_SERVER_ERROR.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), exception.getMessage(), endpoint, INTERNAL_SERVER_ERROR);
  }

  @Test
  public void canGetDatasetSample() {
    Dataset dataset = aPersistedDataset();

    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    DataEnvelope one = anyDataEnvelope();
    DataEnvelope two = anyDataEnvelope();

    when(reader.read(dataset, "", "", "", 10)).thenReturn(Observable.just(one, two));

    List<Map<String, Object>> response = readList("dataset/" + dataset.getId() + "/sample");

    assertThat(response.size(), is(2));
    assertThat(response, hasItem(one.getPayload()));
    assertThat(response, hasItem(two.getPayload()));
  }

  @Test
  public void canHandleFailureWhenGettingADatasetSample() {
    Dataset dataset = aPersistedDataset();

    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    Throwable exception = new RuntimeException("boom!");
    when(reader.read(dataset, "", "", "", 10)).thenReturn(Observable.error(exception));

    String endpoint = "dataset/" + dataset.getId() + "/sample";
    HttpResponse response = read(endpoint);
    assertThat(response.getStatusCode(), is(INTERNAL_SERVER_ERROR.code()));
    assertThat(response.getStatusMessage(), is(INTERNAL_SERVER_ERROR.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), exception.getMessage(), endpoint, INTERNAL_SERVER_ERROR);
  }

  @Test
  public void canGetDatasetContents() {
    Dataset dataset = aPersistedDataset();

    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    DataEnvelope one = anyDataEnvelope();
    DataEnvelope two = anyDataEnvelope();

    String select = "aSelect";
    String where = "aWhere";
    String orderBy = "anOrderBy";

    when(reader.read(dataset, select, where, orderBy, -1)).thenReturn(Observable.just(one, two));

    List<Map<String, Object>> response =
        readList(
            "dataset/"
                + dataset.getId()
                + "/content?select="
                + select
                + "&where="
                + where
                + "&orderBy="
                + orderBy);

    assertThat(response.size(), is(2));
    assertThat(response, hasItem(one.getPayload()));
    assertThat(response, hasItem(two.getPayload()));
  }

  @Test
  public void dodgySubscribeParameterWhenGettingDatasetContents() {
    Dataset dataset = aPersistedDataset();

    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    String endpoint = "dataset/" + dataset.getId() + "/content?subscribe=abc";
    HttpResponse response = read(endpoint);
    assertThat(response.getStatusCode(), is(BAD_REQUEST.code()));
    assertThat(response.getStatusMessage(), is(BAD_REQUEST.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(),
        "Invalid value: abc for parameter: subscribe, valid " + "values are: " + "true|false!",
        endpoint,
        BAD_REQUEST);

    verify(reader, never())
        .read(any(Dataset.class), anyString(), anyString(), anyString(), anyInt());
  }

  @Test
  public void canHandleFailureWhenGettingDatasetContents() {
    Dataset dataset = aPersistedDataset();

    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    Throwable exception = new RuntimeException("boom!");
    when(reader.read(eq(dataset), anyString(), anyString(), anyString(), eq(-1)))
        .thenReturn(Observable.error(exception));

    String endpoint =
        "dataset/" + dataset.getId() + "/content?select=aSelect&where=aWhere&orderBy=anOrderBy";
    HttpResponse response = read(endpoint);
    assertThat(response.getStatusCode(), is(INTERNAL_SERVER_ERROR.code()));
    assertThat(response.getStatusMessage(), is(INTERNAL_SERVER_ERROR.reasonPhrase()));

    assertThatErrorResponseIsCorrect(
        response.getPayload(), exception.getMessage(), endpoint, INTERNAL_SERVER_ERROR);
  }

  @Test
  public void canGetDatasetContentsAndSubscribe() throws Exception {
    Dataset dataset = aPersistedDataset();
    long subscriptionInterval = 500;

    AtomicBoolean subscriptionCompleted = new AtomicBoolean();
    AtomicReference<JsonObject> subscriptionData = new AtomicReference<>();

    subscriber(dataset, subscriptionCompleted, subscriptionData);

    when(datasetDao.exists(dataset.getId())).thenReturn(true);
    when(datasetDao.get(dataset.getId())).thenReturn(dataset);

    DataEnvelope one = anyDataEnvelope();
    DataEnvelope two = anyDataEnvelope();
    DataEnvelope three = anyDataEnvelope();

    String select = "aSelect";
    String where = "aWhere";
    String orderBy = "anOrderBy";

    Observable<DataEnvelope> initialContent = Observable.just(one, two);
    when(reader.read(dataset, select, where, orderBy, -1)).thenReturn(initialContent);
    Observable<DataEnvelope> subsequentContent = Observable.just(three);
    when(reader.read(eq(dataset), eq(select), anyString(), eq(""), eq(-1)))
        .thenReturn(subsequentContent);

    String endpoint =
        "dataset/"
            + dataset.getId()
            + "/content?select="
            + select
            + "&where="
            + where
            + "&orderBy="
            + orderBy
            + "&subscribe=true&subscriptionInterval="
            + subscriptionInterval;
    List<Map<String, Object>> response = readList(endpoint);

    assertThat(response.size(), is(2));
    assertThat(response, hasItem(one.getPayload()));
    assertThat(response, hasItem(two.getPayload()));

    Awaitility.await()
        .atMost((subscriptionInterval + 2500), TimeUnit.MILLISECONDS)
        .until(subscriptionCompleted::get);

    assertThat(
        subscriptionData.get().getMap().get("payload"), is(new JsonObject(three.getPayload())));

    verify(reader, times(2))
        .read(any(Dataset.class), anyString(), anyString(), anyString(), anyInt());
  }

  private void subscriber(
      Dataset dataset,
      AtomicBoolean subscriptionCompleted,
      AtomicReference<JsonObject> subscriptionData) {
    vertx
        .eventBus()
        .consumer(
            dataset.getId(),
            (Handler<Message<JsonObject>>)
                event -> {
                  logger.info("Received pushed content: {}", event.body().toString());

                  if (event.body().containsKey("eventType")) {
                    SubscriptionEvent.SubscriptionEventType eventType =
                        SubscriptionEvent.SubscriptionEventType.valueOf(
                            event.body().getString("eventType"));
                    if (eventType.isTerminal()) {
                      subscriptionCompleted.set(true);
                    } else {
                      if (subscriptionData.get() != null) {
                        // boom because we expect a single publication
                        fail("Dataset contents were published more than once!");
                      } else {
                        // cancel the subscription to facilitate our assertion on cancel
                        delete("dataset/" + dataset.getName() + "/content");

                        subscriptionData.set(event.body());
                      }
                    }
                  } else {
                    logger.warn("The subscription event does not contain an event type!");

                    // exit
                    subscriptionCompleted.set(true);
                  }
                });
  }

  private void assertThatErrorResponseIsCorrect(
      String response,
      String exceptionMessage,
      String endpoint,
      HttpResponseStatus httpResponseStatus) {
    JsonObject errorResponse = new JsonObject(response);

    assertThat(errorResponse.getString("timestamp"), notNullValue());
    assertThat(errorResponse.getInteger("statusCode"), is(httpResponseStatus.code()));
    assertThat(errorResponse.getString("statusMessage"), is(httpResponseStatus.reasonPhrase()));
    MatcherAssert.assertThat(
        errorResponse.getString("path"), Matchers.is(WebServerUtils.withApplicationName(endpoint)));
    assertThat(errorResponse.getString("message"), containsString(exceptionMessage));
    assertThat(errorResponse.getString("stackTrace"), notNullValue());
  }
}
