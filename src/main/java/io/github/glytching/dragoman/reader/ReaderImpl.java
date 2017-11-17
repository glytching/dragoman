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
package io.github.glytching.dragoman.reader;

import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.repository.Repository;
import io.github.glytching.dragoman.repository.router.RepositoryRouter;
import rx.Observable;

import javax.inject.Inject;

/**
 * Implements {@link Reader} by choosing the correct {@link Repository} instance for the
 * given {@link Dataset} and delegating to that repository and the mapping the response to a {@link DataEnvelope}.
 */
public class ReaderImpl implements Reader {

    private final RepositoryRouter repositoryRouter;

    @Inject
    public ReaderImpl(RepositoryRouter repositoryRouter) {
        this.repositoryRouter = repositoryRouter;
    }

    @Override
    public Observable<DataEnvelope> read(Dataset dataset, String select, String where, String orderBy,
                                         Integer maxResults) {
        return repositoryRouter.get(dataset).find(dataset, select, where, orderBy, maxResults)
                .map(incoming -> new DataEnvelope(dataset.getSource(), incoming));
    }
}