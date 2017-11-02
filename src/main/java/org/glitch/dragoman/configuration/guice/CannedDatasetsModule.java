package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.dataset.canned.CannedDatasetsLoader;
import org.glitch.dragoman.dataset.canned.CannedDatasetsLoaderImpl;
import org.glitch.dragoman.dataset.canned.CannedDatasetsWriter;
import org.glitch.dragoman.store.mongo.canned.MongoCannedDatasetsWriter;

public class CannedDatasetsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CannedDatasetsLoader.class).to(CannedDatasetsLoaderImpl.class);
        bind(CannedDatasetsWriter.class).to(MongoCannedDatasetsWriter.class);
    }
}