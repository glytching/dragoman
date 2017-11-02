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
package org.glitch.dragoman.http;

import org.glitch.dragoman.store.http.repository.ResponsePostProcessor;
import org.glitch.dragoman.transform.JsonTransformer;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class HttpClientAdapterImpl implements HttpClientAdapter {

    private final HttpClient httpClient;
    private final JsonTransformer jsonTransformer;

    @Inject
    public HttpClientAdapterImpl(HttpClient httpClient, JsonTransformer jsonTransformer) {
        this.httpClient = httpClient;
        this.jsonTransformer = jsonTransformer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Map<String, Object>> read(String url, ResponsePostProcessor responsePostProcessor) {
        HttpResponse response = httpClient.get(url);

        if (!response.isSuccessful()) {
            throw new HttpClientException(format("Failed to read response from: %s, got: %s", url, response));
        }

        String content = responsePostProcessor.postProcess(response.getPayload());

        List<Map<String, Object>> transformed = jsonTransformer.transform(List.class, content);
        return Observable.from(transformed);
    }
}