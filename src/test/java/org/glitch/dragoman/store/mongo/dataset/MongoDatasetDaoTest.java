package org.glitch.dragoman.store.mongo.dataset;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.glitch.dragoman.configuration.guice.ConfigurationModule;
import org.glitch.dragoman.configuration.guice.DatasetModule;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.dataset.DatasetDao;
import org.glitch.dragoman.store.mongo.AbstractMongoDBTest;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.repository.MongoOverrideModule;
import org.glitch.dragoman.util.RandomValues;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.format;
import static org.glitch.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class MongoDatasetDaoTest extends AbstractMongoDBTest {

    @Inject
    private DatasetDao datasetDao;
    @Inject
    private MongoProvider mongoProvider;

    @Before
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
    public void canGet() {
        Dataset incoming = aDataset();

        Dataset written = write(incoming);

        assertThat(datasetDao.get(written.getId()), is(incoming));
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
        return anyDataset(RandomValues.aString());
    }
}