package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.dataset.DatasetDao;
import org.glitch.dragoman.store.mongo.dataset.MongoDatasetDao;

public class DatasetModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DatasetDao.class).to(MongoDatasetDao.class);
    }
}