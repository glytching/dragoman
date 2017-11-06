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
package org.glytching.dragoman.store.mongo.canned;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.FindObservable;
import com.mongodb.rx.client.MongoCollection;
import org.bson.Document;
import org.glytching.dragoman.configuration.ApplicationConfiguration;
import org.glytching.dragoman.configuration.guice.CannedDatasetsModule;
import org.glytching.dragoman.configuration.guice.ConfigurationModule;
import org.glytching.dragoman.configuration.guice.DatasetModule;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.dataset.canned.CannedDataset;
import org.glytching.dragoman.dataset.canned.CannedDatasetsLoader;
import org.glytching.dragoman.dataset.canned.CannedDatasetsWriter;
import org.glytching.dragoman.store.mongo.AbstractMongoDBTest;
import org.glytching.dragoman.store.mongo.DocumentTransformer;
import org.glytching.dragoman.store.mongo.MongoProvider;
import org.glytching.dragoman.store.mongo.MongoStorageCoordinates;
import org.glytching.dragoman.store.mongo.repository.MongoOverrideModule;
import org.glytching.dragoman.util.RandomValues;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.glytching.dragoman.util.TestFixture.anyDocument;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class MongoCannedDatasetsWriterTest extends AbstractMongoDBTest {

    @Inject
    private ApplicationConfiguration configuration;
    @Inject
    private CannedDatasetsLoader cannedDatasetsLoader;
    @Inject
    private MongoProvider mongoProvider;
    @Inject
    private CannedDatasetsWriter cannedDatasetsWriter;
    @Inject
    private DocumentTransformer documentTransformer;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(Modules.override(new DatasetModule(), new CannedDatasetsModule(),
                new ConfigurationModule()).with(new MongoOverrideModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(CannedDatasetsLoader.class).toInstance(Mockito.mock(CannedDatasetsLoader.class));
            }
        }));
        injector.injectMembers(this);

        when(mongoProvider.provide()).thenReturn(getMongoClient());
    }

    @Test
    public void canWriteCannedDatasets() {
        CannedDataset one =
                new CannedDataset(anyDataset(new MongoStorageCoordinates("a:b")), Lists.newArrayList(anyDocument(),
                        anyDocument()));
        CannedDataset two =
                new CannedDataset(new Dataset(RandomValues.aString(), RandomValues.aString(), "http://host:1234/some/end/point"), null);
        when(cannedDatasetsLoader.load(configuration.getCannedDatasetsDirectory())).thenReturn(Lists.newArrayList
                (one, two));

        int writeCount = cannedDatasetsWriter.write();

        assertThat(writeCount, is(2));

        // now verify that the datasets and their documents have been written
        assertDatasetIsWritten(one);
        assertDatasetContentsAreWritten(one);

        assertDatasetIsWritten(two);
        assertDatasetContentsAreWritten(two);
    }

    @Test
    public void canHandleNoCannedDatasets() {
        when(cannedDatasetsLoader.load(configuration.getCannedDatasetsDirectory())).thenReturn(Collections.emptyList());

        int writeCount = cannedDatasetsWriter.write();

        assertThat(writeCount, is(0));
    }

    private void assertDatasetIsWritten(CannedDataset cannedDataset) {
        FindObservable<Document> datasets =
                getCollection(configuration.getDatabaseName(), configuration.getDatasetStorageName()).
                        find(Filters.eq("name", cannedDataset.getDataset().getName()));

        assertThat(documentTransformer.transform(Dataset.class, datasets.first().toBlocking().single()),
                is(cannedDataset.getDataset()));
    }

    private void assertDatasetContentsAreWritten(CannedDataset cannedDataset) {
        MongoCollection<Document> collection =
                getCollection(new MongoStorageCoordinates(cannedDataset.getDataset().getSource()));

        if (cannedDataset.getDocuments() != null) {
            assertThat(collection.count().toBlocking().single(), is((long) cannedDataset.getDocuments().size()));

            List<Document> actualDocuments = collection.find().toObservable().map(document -> {
                // remove the persisted _id to allow comparison with the unpersisted 'expected' documents
                document.remove("_id");
                return document;
            }).toList().toBlocking().single();

            for (Map<String, Object> expected : cannedDataset.getDocuments()) {
                assertThat(actualDocuments, hasItem(new Document(expected)));
            }
        }
    }

    private MongoCollection<Document> getCollection(MongoStorageCoordinates storageCoordinates) {
        return mongoProvider.provide()
                .getDatabase(storageCoordinates.getDatabaseName())
                .getCollection(storageCoordinates.getCollectionName());
    }

    private MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        return mongoProvider.provide()
                .getDatabase(databaseName)
                .getCollection(collectionName);
    }
}