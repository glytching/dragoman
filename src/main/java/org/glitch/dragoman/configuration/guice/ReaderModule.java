package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.reader.Reader;
import org.glitch.dragoman.reader.ReaderImpl;

public class ReaderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Reader.class).to(ReaderImpl.class);
    }
}