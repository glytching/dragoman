package org.glitch.dragoman.web.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.awaitility.Awaitility;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.dataset.DatasetDao;
import org.glitch.dragoman.http.HttpResponse;
import org.glitch.dragoman.reader.DataEnvelope;
import org.glitch.dragoman.reader.Reader;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.glitch.dragoman.util.TestFixture.*;
import static org.glitch.dragoman.web.WebServerUtils.withApplicationName;
import static org.glitch.dragoman.web.subscription.SubscriptionEvent.SubscriptionEventType;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class DatasetResourceTest extends AbstractResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(DatasetResourceTest.class);

    @Inject
    private DatasetDao datasetDao;
    @Inject
    private Reader reader;
    @Inject
    private Vertx vertx;
    @Inject
    private ApplicationConfiguration applicationConfiguration;

    @Test
    public void canGetAllDatasets() throws IOException {
        Dataset one = anyDataset();
        Dataset two = anyDataset();

        when(datasetDao.getAll(anyString())).thenReturn(Observable.just(one, two));

        HttpResponse response = read("datasets");
        assertThat(response.getStatusCode(), is(HttpResponseStatus.OK.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.OK.reasonPhrase()));

        // cannot use the injected converter here because we are using a jackson specific TypeReference
        List<Dataset> datasets =
                new ObjectMapper().readValue(response.getPayload(), new TypeReference<List<Dataset>>() {
                });
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
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));

        assertThatErrorResponseIsCorrect(response.getPayload(), exception.getMessage(), endpoint,
                INTERNAL_SERVER_ERROR);
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
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));

        assertThatErrorResponseIsCorrect(response.getPayload(), exception.getMessage(), endpoint,
                INTERNAL_SERVER_ERROR);
    }

    @Test
    public void canCreateDataset() {
        Dataset dataset = anyDataset();

        String name = "aName";
        String source = "aSource";
        String subscriptionControlField = "aSubscriptionControlField";
        String subscriptionControlFieldPattern = "aSubscriptionControlFieldPattern";

        when(datasetDao.write(any(Dataset.class))).thenReturn(dataset);

        String payload =
                "{ \"name\": \"" + name + "\", \"source\": \"" + source + "\", " +
                        "\"subscriptionControlField\": \"" + subscriptionControlField + "\", " +
                        "\"subscriptionControlFieldPattern\": \"" + subscriptionControlFieldPattern + "\" }";

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
        assertThat(submittedDataset.getSubscriptionControlFieldPattern(), is(subscriptionControlFieldPattern));
    }

    @Test
    public void canHandleFailureWhenWritingADataset() {
        String name = "aName";
        String source = "aSource";

        Exception exception = new RuntimeException("boom!");
        when(datasetDao.write(any(Dataset.class))).thenThrow(exception);

        String payload =
                "{ \"name\": \"" + name + "\", \"source\": \"" + source + "\" }";

        String endpoint = "dataset";
        HttpResponse response = post(endpoint, payload);
        assertThat(response.getStatusCode(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()));

        assertThatErrorResponseIsCorrect(response.getPayload(), exception.getMessage(), endpoint,
                INTERNAL_SERVER_ERROR);
    }

    @Test
    public void dodgyBodyWillBeRejectedAsABadRequestWhenAttemptingToCreateDataset() {
        String endpoint = "dataset";
        HttpResponse response = post(endpoint, "{ \"foo\": \"whatever\" }");
        assertThat(response.getStatusCode(), is(HttpResponseStatus.BAD_REQUEST.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.BAD_REQUEST.reasonPhrase()));

        assertThatErrorResponseIsCorrect(response.getPayload(), "Failed to deserialise request body", endpoint,
                BAD_REQUEST);

        verify(datasetDao, never()).write(any(Dataset.class));
    }

    @Test
    public void canUpdateDataset() {
        Dataset dataset = anyDataset();

        String name = "aName";
        String source = "aSource";

        when(datasetDao.write(any(Dataset.class))).thenReturn(dataset);

        String payload =
                "{ \"name\": \"" + name + "\", \"source\": \"" + source + "\" }";

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

        assertThatErrorResponseIsCorrect(response.getPayload(), "Failed to deserialise request body", endpoint,
                BAD_REQUEST);

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

        assertThatErrorResponseIsCorrect(response.getPayload(), exception.getMessage(), endpoint,
                INTERNAL_SERVER_ERROR);
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

        assertThatErrorResponseIsCorrect(response.getPayload(), exception.getMessage(), endpoint,
                INTERNAL_SERVER_ERROR);
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
                readList("dataset/" + dataset.getId() + "/content?select=" + select + "&where=" + where + "&orderBy=" + orderBy);

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

        assertThatErrorResponseIsCorrect(response.getPayload(), "Invalid value: abc for parameter: subscribe, valid " +
                "values are: " +
                "true|false!", endpoint, BAD_REQUEST);

        verify(reader, never()).read(any(Dataset.class), anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    public void canHandleFailureWhenGettingDatasetContents() {
        Dataset dataset = aPersistedDataset();

        when(datasetDao.get(dataset.getId())).thenReturn(dataset);

        Throwable exception = new RuntimeException("boom!");
        when(reader.read(eq(dataset), anyString(), anyString(), anyString(), eq(-1))).thenReturn(Observable.error
                (exception));

        String endpoint = "dataset/" + dataset.getId() + "/content?select=aSelect&where=aWhere&orderBy=anOrderBy";
        HttpResponse response = read(endpoint);
        assertThat(response.getStatusCode(), is(INTERNAL_SERVER_ERROR.code()));
        assertThat(response.getStatusMessage(), is(INTERNAL_SERVER_ERROR.reasonPhrase()));

        assertThatErrorResponseIsCorrect(response.getPayload(), exception.getMessage(), endpoint,
                INTERNAL_SERVER_ERROR);
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
        when(reader.read(eq(dataset), eq(select), anyString(), eq(""), eq(-1))).thenReturn(subsequentContent);

        String endpoint = "dataset/" + dataset.getId() + "/content?select=" + select + "&where=" + where +
                "&orderBy=" + orderBy + "&subscribe=true&subscriptionInterval=" + subscriptionInterval;
        List<Map<String, Object>> response = readList(endpoint);

        assertThat(response.size(), is(2));
        assertThat(response, hasItem(one.getPayload()));
        assertThat(response, hasItem(two.getPayload()));

        Awaitility.await().atMost((subscriptionInterval + 2500), TimeUnit.MILLISECONDS)
                .until(subscriptionCompleted::get);

        assertThat(subscriptionData.get().getMap().get("payload"), is(new JsonObject(three.getPayload())));

        verify(reader, times(2)).read(any(Dataset.class), anyString(), anyString(), anyString(), anyInt());
    }

    private void subscriber(Dataset dataset, AtomicBoolean subscriptionCompleted,
                            AtomicReference<JsonObject> subscriptionData) {
        vertx.eventBus().consumer(dataset.getId(), (Handler<Message<JsonObject>>) event -> {
            logger.info("Received pushed content: {}", event.body().toString());

            if (event.body().containsKey("eventType")) {
                SubscriptionEventType eventType = SubscriptionEventType.valueOf(event.body().getString("eventType"));
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

    private void assertThatErrorResponseIsCorrect(String response, String exceptionMessage, String endpoint,
                                                  HttpResponseStatus httpResponseStatus) {
        JsonObject errorResponse = new JsonObject(response);

        assertThat(errorResponse.getString("timestamp"), notNullValue());
        assertThat(errorResponse.getInteger("statusCode"), is(httpResponseStatus.code()));
        assertThat(errorResponse.getString("statusMessage"), is(httpResponseStatus.reasonPhrase()));
        assertThat(errorResponse.getString("path"), is(withApplicationName(endpoint)));
        assertThat(errorResponse.getString("message"), containsString(exceptionMessage));
        assertThat(errorResponse.getString("stackTrace"), notNullValue());
    }
}