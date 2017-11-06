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
package org.glytching.dragoman.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.glytching.dragoman.store.http.repository.NoOpResponsePostProcessor;
import org.glytching.dragoman.transform.JsonTransformer;
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

import static org.glytching.dragoman.util.TestFixture.anyMap;
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