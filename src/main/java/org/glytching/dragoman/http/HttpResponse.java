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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * A simple wrapper over a raw HTTP response. Layers within the application which are not HTTP-aware can then handle the
 * response based on its status code, headers, payload etc without having to know about our HTTP integration, without
 * needing a HTTP client etc.
 */
public class HttpResponse {

    private final int statusCode;
    private final String statusMessage;
    private final String url;
    private final Map<String, List<String>> headers;
    private final String payload;

    public HttpResponse(int statusCode, String statusMessage, String url, Map<String, List<String>> headers,
                        String payload) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.url = url;
        this.headers = headers;
        this.payload = payload;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
