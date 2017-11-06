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
package org.glytching.dragoman.store.http.repository;

/**
 * This application expects HTTP sources to emit JSON responses. Where that's not the case, a dataset may registered
 * with a response post processor. It is the response post processor's job to transform the raw repsonse into something
 * which this application can hand;e.
 */
public interface ResponsePostProcessor {

    /**
     * Process a raw HTTP response, ensuring that the result meets this application's expectations for HTTP responses,
     * namely; valid JSON.
     *
     * @param content a raw response from a HTTP data source
     *
     * @return transformed {@code content}
     */
    String postProcess(String content);
}
