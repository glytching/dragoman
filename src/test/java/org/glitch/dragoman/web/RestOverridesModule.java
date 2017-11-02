package org.glitch.dragoman.web;

import com.google.inject.AbstractModule;
import org.glitch.dragoman.authentication.AuthenticationDao;
import org.glitch.dragoman.dataset.DatasetDao;
import org.glitch.dragoman.reader.Reader;
import org.glitch.dragoman.repository.router.RepositoryRouter;

import static org.mockito.Mockito.mock;

public class RestOverridesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RepositoryRouter.class).toInstance(mock(RepositoryRouter.class));
        bind(DatasetDao.class).toInstance(mock(DatasetDao.class));
        bind(AuthenticationDao.class).toInstance(mock(AuthenticationDao.class));
        bind(Reader.class).toInstance(mock(Reader.class));
    }
}