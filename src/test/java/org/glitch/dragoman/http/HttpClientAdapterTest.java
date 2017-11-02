package org.glitch.dragoman.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.glitch.dragoman.store.http.repository.NoOpResponsePostProcessor;
import org.glitch.dragoman.transform.JsonTransformer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.glitch.dragoman.util.TestFixture.anyMap;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientAdapterTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private HttpClient httpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NoOpResponsePostProcessor responsePostProcessor = new NoOpResponsePostProcessor();
    private final String url = "aUrl";

    private HttpClientAdapter httpClientAdapter;

    @Before
    public void setUp() {
        httpClientAdapter = new HttpClientAdapterImpl(httpClient, new JsonTransformer(objectMapper));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canHandleSuccessfulResponse() throws Exception {
        Map<String, Object> one = anyMap();
        Map<String, Object> two = anyMap();

        when(httpClient.get(url)).thenReturn(successfulResponse(one, two));

        List<Map<String, Object>> items = toList(httpClientAdapter.read(url, responsePostProcessor));

        assertThat(items.size(), is(2));
        assertThat(items, hasItem(one));
        assertThat(items, hasItem(two));
    }

    @Test
    public void canHandleFailedResponse() {
        String httpExceptionMessage = "boom!";
        expectedException.expect(HttpClientException.class);
        expectedException.expectMessage(startsWith("Failed to read response from: " + url));
        expectedException.expectMessage(containsString(httpExceptionMessage));

        when(httpClient.get(url)).thenReturn(failedResponse(httpExceptionMessage));

        httpClientAdapter.read(url, responsePostProcessor);
    }

    @SafeVarargs
    private final HttpResponse successfulResponse(Map<String, Object>... payloads) throws JsonProcessingException {
        return new HttpResponse(200, "", "", new HashMap<>(), objectMapper.writeValueAsString(Lists.newArrayList(payloads)));
    }

    private HttpResponse failedResponse(String failureMessage) {
        return new HttpResponse(500, failureMessage, "", new HashMap<>(), "");
    }

    private List<Map<String, Object>> toList(Observable<Map<String, Object>> incoming) {
        return incoming.toList().toBlocking().single();
    }
}