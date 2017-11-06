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

import org.glytching.dragoman.dataset.Dataset;

/**
 * A factory for creating a {@link ResponsePostProcessor} instance for a given {@link Dataset}.
 */
public class ResponsePostProcessorFactory {
    private static final ResponsePostProcessor DEFAULT_RESPONSE_POST_PROCESSOR = new ListAwareResponsePostProcessor();

    /**
     * Returns an instance of {@link ResponsePostProcessor} appropriate to the given {@code dataset}. Currently, there
     * is only one implementation of {@link ResponsePostProcessor} so this factory looks a little bit silly.
     *
     * @param dataset
     *
     * @return
     */
    public ResponsePostProcessor create(@SuppressWarnings("unused") Dataset dataset) {
        return DEFAULT_RESPONSE_POST_PROCESSOR;
    }
}