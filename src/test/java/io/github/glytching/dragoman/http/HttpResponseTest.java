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
package io.github.glytching.dragoman.http;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HttpResponseTest {

    private final String message = "aMessage";
    private final String url = "aUrl";
    private final HashMap<String, List<String>> headers = new HashMap<>();
    private final String payload = "aPayload";

    @Test
    public void canWrapASuccessfulResponse() {
        int statusCode = 200;

        HttpResponse httpResponse = new HttpResponse(statusCode, message, url, headers, payload);

        assertThat(httpResponse.getStatusCode(), is(statusCode));
        assertThat(httpResponse.getStatusMessage(), is(message));
        assertThat(httpResponse.getUrl(), is(url));
        assertThat(httpResponse.getHeaders(), is(headers));
        assertThat(httpResponse.getPayload(), is(payload));
        assertThat(httpResponse.isSuccessful(), is(true));
    }

    @Test
    public void canWrapAnUnsuccessfulResponse() {
        int statusCode = 404;

        HttpResponse httpResponse = new HttpResponse(statusCode, message, url, headers, payload);

        assertThat(httpResponse.getStatusCode(), is(statusCode));
        assertThat(httpResponse.getStatusMessage(), is(message));
        assertThat(httpResponse.getUrl(), is(url));
        assertThat(httpResponse.getHeaders(), is(headers));
        assertThat(httpResponse.getPayload(), is(payload));
        assertThat(httpResponse.isSuccessful(), is(false));
    }
}