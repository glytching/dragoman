package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.glitch.dragoman.repository.Repository;
import org.glitch.dragoman.repository.router.RepositoryRouter;
import org.glitch.dragoman.repository.router.RepositoryRouterImpl;
import org.glitch.dragoman.store.http.repository.HttpRepository;
import org.glitch.dragoman.store.mongo.repository.DecoratingMongoRepository;

public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Repository> multibinder = Multibinder.newSetBinder(binder(), Repository.class);
        multibinder.addBinding().to(HttpRepository.class);
        multibinder.addBinding().to(DecoratingMongoRepository.class);

        bind(RepositoryRouter.class).to(RepositoryRouterImpl.class);
    }
}