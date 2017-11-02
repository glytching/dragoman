package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.authentication.AuthenticationDao;
import org.glitch.dragoman.store.mongo.authentication.MongoAuthenticationDao;

public class AuthenticationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthenticationDao.class).to(MongoAuthenticationDao.class);
    }
}