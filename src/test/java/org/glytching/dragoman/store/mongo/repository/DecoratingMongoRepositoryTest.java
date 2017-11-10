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
package org.glytching.dragoman.store.mongo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.store.mongo.DocumentTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;

import java.util.List;
import java.util.Map;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.glytching.dragoman.util.TestFixture.anyDocument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SuppressWarnings("FieldCanBeLocal")
public class DecoratingMongoRepositoryTest {

    @Mock
    private MongoRepository delegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String select = "aSelect";
    private final String where = "aWhere";
    private final String orderBy = "nOrderBy";
    private final int maxResults = 50;

    private Dataset dataset;
    private DocumentTransformer documentTransformer;
    private DecoratingMongoRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        dataset = anyDataset();

        documentTransformer = new DocumentTransformer(objectMapper);

        repository = new DecoratingMongoRepository(delegate, documentTransformer);
    }

    @Test
    public void appliesToWhateverTheDelegateAppliesTo() {
        Dataset dataset = anyDataset();

        when(delegate.appliesTo(dataset)).thenReturn(true);
        assertThat(repository.appliesTo(dataset), is(true));

        when(delegate.appliesTo(dataset)).thenReturn(false);
        assertThat(repository.appliesTo(dataset), is(false));
    }

    @Test
    public void willDelegateThenTransformTheResponse() {
        Document one = anyDocument();
        Document two = anyDocument();

        when(delegate.find(dataset, select, where, orderBy, maxResults)).thenReturn(Observable.just(one, two));

        List<Map<String, Object>> results = toList(repository.find(dataset, select, where, orderBy, maxResults));

        assertThat(results.size(), is(2));
        assertThat(results, hasItem(documentTransformer.transform(one)));
        assertThat(results, hasItem(documentTransformer.transform(two)));
    }

    private List<Map<String, Object>> toList(Observable<Map<String, Object>> observable) {
        return observable.toList().toBlocking().single();
    }

}
