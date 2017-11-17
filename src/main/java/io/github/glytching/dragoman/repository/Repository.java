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
package io.github.glytching.dragoman.repository;

import io.github.glytching.dragoman.dataset.Dataset;
import rx.Observable;

/**
 * Defines the interaction with our repository layer. Implementations of this interface are expected to be specific
 * to a data source class e.g. MongoDB, HTTP etc.
 */
public interface Repository<T> {

    /**
     * Read data from the given {@code dataset}'s source, ensuring that the result is faithful to the supplied
     * {@code select}, {@code where} etc.
     *
     * @param dataset the {@link Dataset} to be queried
     * @param select the projections (if any) to be applied to the data in the requested dataset
     * @param where the predicates (if any) to be used when filtering the requested dataset
     * @param orderBy the ordering (if any) to be applied to the data read from the requested dataset
     * @param maxResults a limit on the number of entries to be read from the requested dataset
     *
     * @return an observable over the data selected from the given {@code dataset}'s source
     */
    Observable<T> find(Dataset dataset, String select, String where, String orderBy, int maxResults);

    /**
     * Is this repository instance relevant to the given {@code dataset}. Every dataset can be handled by at most one
     * repository type, with this method providing the answer to this question: 'can you handle this dataset?'.
     *
     * @param dataset the {@link Dataset} to be tested for applicability
     *
     * @return true if this repository instance can handle the given {@code dataset}, false otherwise
     */
    boolean appliesTo(Dataset dataset);
}
