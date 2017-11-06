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
import org.glytching.dragoman.repository.Repository;
import org.glytching.dragoman.repository.router.RepositoryRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.Observable;

import java.util.List;
import java.util.Map;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.glytching.dragoman.util.TestFixture.anyMap;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class MongoReaderTest {

    @Mock
    private RepositoryRouter repositoryRouter;
    @Mock
    private Repository<Map<String, Object>> repository;

    private Reader reader;

    private Dataset dataset;

    private final String select = "aSelect";
    private final String where = "aWhere";
    private final String orderBy = "anOrderBy";

    @Before
    public void setUp() {
        dataset = anyDataset();

        when(repositoryRouter.get(dataset)).thenReturn(repository);

        reader = new ReaderImpl(repositoryRouter);
    }

    @Test
    public void canRead() {
        Map<String, Object> one = anyMap();
        Map<String, Object> two = anyMap();

        Observable<Map<String, Object>> response = Observable.just(one, two);

        when(repository.find(dataset, select, where, orderBy, -1)).thenReturn(response);

        List<DataEnvelope> dataEnvelopes = reader.read(dataset, select, where, orderBy, -1)
                .toList()
                .toBlocking()
                .single();

        assertThat(dataEnvelopes.size(), is(2));
        assertThat(dataEnvelopes, hasItem(new DataEnvelope(dataset.getSource(), one)));
        assertThat(dataEnvelopes, hasItem(new DataEnvelope(dataset.getSource(), two)));
    }
}