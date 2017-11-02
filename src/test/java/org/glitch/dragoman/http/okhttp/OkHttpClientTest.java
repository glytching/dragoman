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
package org.glitch.dragoman.http.okhttp;

import okhttp3.*;
import org.glitch.dragoman.http.HttpClientException;
import org.glitch.dragoman.http.HttpResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OkHttpClientTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private okhttp3.OkHttpClient _httpClient;

    private final String url = "http://host:1234/some/end/point";
    private final String payload = "aPayload";
    private final String json = "{\"a\": \"b\"}";

    private OkHttpClient okHttpClient;

    @Before
    public void setUp() {
        okHttpClient = new OkHttpClient(_httpClient);
    }

    @Test
    public void canGet() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = aSuccessfulResponse(request, payload);

        expectOkHttpClientRequest(response);

        HttpResponse httpResponse = okHttpClient.get(url);

        assertThatResponseIsCorrect(httpResponse, response, true, payload);

        assertThatCorrectRequestIsSubmitted(_httpClient, url, "GET");
    }

    @Test
    public void canDelete() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        Response response = aSuccessfulResponse(request, payload);

        expectOkHttpClientRequest(response);

        HttpResponse httpResponse = okHttpClient.delete(url);

        assertThatResponseIsCorrect(httpResponse, response, true, payload);

        assertThatCorrectRequestIsSubmitted(_httpClient, url, "DELETE");
    }

    @Test
    public void canPost() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();

        Response response = aSuccessfulResponse(request, payload);

        expectOkHttpClientRequest(response);

        HttpResponse httpResponse = okHttpClient.post(url, json);

        assertThatResponseIsCorrect(httpResponse, response, true, payload);

        assertThatCorrectRequestIsSubmitted(_httpClient, url, "POST");
    }

    @Test
    public void canPut() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();

        Response response = aSuccessfulResponse(request, payload);

        expectOkHttpClientRequest(response);

        HttpResponse httpResponse = okHttpClient.put(url, json);

        assertThatResponseIsCorrect(httpResponse, response, true, payload);

        assertThatCorrectRequestIsSubmitted(_httpClient, url, "PUT");
    }

    @Test
    public void canHandleFailure() throws IOException {
        IOException exception = new IOException("boom!");
        expectedException.expect(HttpClientException.class);
        expectedException.expectMessage(containsString("Failed to read from: " + url + ", caused by: " + exception.getMessage()));

        Call call = mock(Call.class);
        when(_httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(exception);

        okHttpClient.get(url);
    }

    private void expectOkHttpClientRequest(Response response) throws IOException {
        Call call = mock(Call.class);

        // ok http's Request does not play ball with eq() so we allow anything through here and
        // then assert that the expected Request was submitted in a later verification call
        when(_httpClient.newCall(any(Request.class))).thenReturn(call);

        when(call.execute()).thenReturn(response);
    }

    @SuppressWarnings("SameParameterValue")
    private void assertThatResponseIsCorrect(HttpResponse actual, Response expected, boolean successful,
                                             String payload) {
        assertThat(actual.isSuccessful(), is(successful));
        assertThat(actual.getStatusCode(), is(expected.code()));
        assertThat(actual.getStatusMessage(), is(expected.message()));
        assertThat(actual.getHeaders(), is(expected.headers().toMultimap()));
        assertThat(actual.getUrl(), is(url));
        assertThat(actual.getPayload(), is(payload));
    }

    private void assertThatCorrectRequestIsSubmitted(okhttp3.OkHttpClient httpClient, String url, String method) {
        ArgumentCaptor<Request> argumentCaptor = ArgumentCaptor.forClass(Request.class);

        verify(httpClient, times(1)).newCall(argumentCaptor.capture());

        Request actualRequest = argumentCaptor.getValue();
        assertThat(actualRequest.method(), is(method));
        assertThat(actualRequest.url(), is(HttpUrl.parse(url)));
    }

    private Response aSuccessfulResponse(Request request, String payload) {
        return new Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_1_0)
                .message("aMessage")
                .request(request)
                .header("a", "b")
                .body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), payload))
                .build();
    }


}
