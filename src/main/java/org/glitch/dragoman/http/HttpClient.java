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

/**
 * Defines the interaction with HTTP data sources. Implementations of this interface are expected to be specific
 * to a chosen HTTP client technology e.g. OkHttp, Apache HTTP Client etc.
 */
public interface HttpClient {

    /**
     * Executes a {@code get} against the goven {@code url}.
     *
     * @param url the url to be addressed
     *
     * @return a {@link HttpResponse} wrapping the details from the raw HTTP response
     */
    HttpResponse get(String url);

    /**
     * Executes a {@code delete} against the goven {@code url}.
     *
     * @param url the url to be addressed
     *
     * @return a {@link HttpResponse} wrapping the details from the raw HTTP response
     */
    HttpResponse delete(String url);

    /**
     * Executes a {@code post} against the goven {@code url}.
     *
     * @param url the url to be addressed
     *
     * @return a {@link HttpResponse} wrapping the details from the raw HTTP response
     */
    HttpResponse post(String url, String json);

    /**
     * Executes a {@code put} against the goven {@code url}.
     *
     * @param url the url to be addressed
     *
     * @return a {@link HttpResponse} wrapping the details from the raw HTTP response
     */
    HttpResponse put(String url, String json);
}