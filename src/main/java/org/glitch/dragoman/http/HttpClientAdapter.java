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
import rx.Observable;

import java.util.Map;

/**
 * Defines the responsibilties for a facade which sits between a raw {@link HttpClient} and a
 * {@link org.glitch.dragoman.store.http.repository.HttpRepository}. The former returns a string reponse (JSON, for
 * example) whereas the latter operates on an {@code Observable<Map<String, Object>>}. This cannot be part of the
 * {@link org.glitch.dragoman.repository.Repository} hierarchy (e.g. a decorating form of the
 * {@link org.glitch.dragoman.store.http.repository.HttpRepository}) because it knows nothing about projections,
 * predicates etc instead it just knows about a URL.
 */
public interface HttpClientAdapter {

    Observable<Map<String, Object>> read(String url, ResponsePostProcessor responsePostProcessor);
}