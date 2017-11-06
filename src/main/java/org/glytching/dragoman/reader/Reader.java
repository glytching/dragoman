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
package org.glytching.dragoman.reader;

import org.glytching.dragoman.dataset.Dataset;
import rx.Observable;

/**
 * This is the main entry point for all reads; delegates the given query details (predicates, projections etc) to the underlying
 * repository layer and emits an Observable over the results.
 */
public interface Reader {

    /**
     * Gets an observable over a resultset. Most of the arguments here are optinal; if none are provided then the entire
     * resultset is retrieved. So, yes, in general, it makes sense to use the arguments to (a) filter the resultset and
     * (b) shape the resultset.
     *
     * @param dataset the {@link Dataset} to be queried
     * @param select the projections (if any) to be applied to the data in the requested dataset
     * @param where the predicates (if any) to be used when filtering the requested dataset
     * @param orderBy the ordering (if any) to be applied to the data read from the requested dataset
     * @param maxResults a limit on the number of entries to be read from the requested dataset
     *
     * @return an observable over the dataset identified by the given {@code dataset}, {@code select}, {@code where} etc
     */
    Observable<DataEnvelope> read(Dataset dataset, String select, String where, String orderBy, Integer maxResults);
}