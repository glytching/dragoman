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
package org.glytching.dragoman.store.mongo.dataset;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.glytching.dragoman.configuration.guice.ConfigurationModule;
import org.glytching.dragoman.configuration.guice.DatasetModule;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.dataset.DatasetDao;
import org.glytching.dragoman.store.mongo.AbstractMongoDBTest;
import org.glytching.dragoman.store.mongo.MongoProvider;
import org.glytching.dragoman.store.mongo.repository.MongoOverrideModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.format;
import static org.glytching.dragoman.util.TestFixture.aString;
import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class MongoDatasetDaoTest extends AbstractMongoDBTest {

    @Inject
    private DatasetDao datasetDao;
    @Inject
    private MongoProvider mongoProvider;

    @BeforeEach
    public void setUp() {
        Injector injector =
                Guice.createInjector(Modules.override(new DatasetModule(), new ConfigurationModule()).with(new MongoOverrideModule()));
        injector.injectMembers(this);

        when(mongoProvider.provide()).thenReturn(getMongoClient());
    }

    @Test
    public void canCreate() {
        Dataset dataset = aDataset();

        Dataset actual = write(dataset);

        assertThat(actual, is(dataset));
    }

    @Test
    public void existsIfThereIsADatasetForTheGivenId() {
        Dataset dataset = aDataset();

        Dataset actual = write(dataset);

        assertThat(datasetDao.exists(actual.getId()), is(true));
    }

    @Test
    public void doesNotExistsIfThereIsNoDatasetForTheGivenId() {
        assertThat(datasetDao.exists(aString()), is(false));
    }

    @Test
    public void canUpdate() {
        Dataset dataset = aDataset();

        // write it
        Dataset written = write(dataset);

        String datasetId = written.getId();

        // update it
        String source = "foo";
        dataset.setSource(source);

        Dataset updated = write(dataset);

        assertThat(updated.getSource(), is(source));
        assertThat(updated, is(dataset));
        assertThat(updated.getId(), is(datasetId));
    }

    @Test
    public void canDelete() {
        Dataset dataset = aDataset();

        write(dataset);

        long deleteCount = datasetDao.delete(dataset.getId());

        assertThat(deleteCount, is(1L));
        assertThat(exists(dataset), is(false));
    }

    @Test
    public void willReturnZeroIfAskedToDeleteADatasetWhichDoesNotExist() {
        assertThat(datasetDao.delete(aString()), is(0L));
    }

    @Test
    public void canGet() {
        Dataset incoming = aDataset();

        Dataset written = write(incoming);

        assertThat(datasetDao.get(written.getId()), is(incoming));
    }

    @Test
    public void willReturnNullIfAskedToGetADatasetWhichDoesNotExist() {
        assertThat(datasetDao.get(aString()), nullValue());
    }

    @Test
    public void canGetAll() {
        String user = "aUser";
        Dataset one = anyDataset(user);
        Dataset two = anyDataset(user);
        Dataset three = aDataset();

        // write them
        write(one, two, three);

        Observable<Dataset> all = datasetDao.getAll(one.getOwner());

        List<Dataset> datasets = all.toList().toBlocking().single();

        assertThat(datasets, hasSize(2));
        assertThat(datasets, hasItem(one));
        assertThat(datasets, hasItem(two));
    }

    @Test
    public void canGetAllWhenNoneExist() {
        Observable<Dataset> all = datasetDao.getAll(aString());

        List<Dataset> datasets = all.toList().toBlocking().single();

        assertThat(datasets, hasSize(0));
    }

    private boolean exists(Dataset dataset) {
        return datasetDao.exists(dataset.getId());
    }

    private void write(Dataset... datasets) {
        for (Dataset dataset : datasets) {
            write(dataset);
        }
    }

    private Dataset write(Dataset dataset) {
        Dataset written = datasetDao.write(dataset);

        assertThat(written, notNullValue());

        // fail early if the write has not done what we expect it to do ...
        assertThat(format("Failed to write dataset: %s", dataset), exists(written), is(true));

        return datasetDao.get(written.getId());
    }

    private Dataset aDataset() {
        return anyDataset(aString());
    }
}