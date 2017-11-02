package org.glitch.dragoman.repository.router;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class RepositoryRouterImpl implements RepositoryRouter {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryRouterImpl.class);

    private final LoadingCache<Dataset, Optional<Repository>> repositoryCache;

    @Inject
    public RepositoryRouterImpl(Set<Repository> repositories) {
        this.repositoryCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new RepositoryCacheLoader(repositories));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Repository<Map<String, Object>> get(Dataset dataset) {
        try {
            return repositoryCache.get(dataset).orElseThrow(() -> new NoRepositoryAvailableException(dataset));
        } catch (ExecutionException ex) {
            throw new NoRepositoryAvailableException(format("Could not find a repository for: %s!", dataset), ex);
        }
    }

    private class RepositoryCacheLoader extends CacheLoader<Dataset, Optional<Repository>> {
        private final Set<Repository> repositories;

        private RepositoryCacheLoader(Set<Repository> repositories) {
            this.repositories = repositories;
        }

        @SuppressWarnings({"unchecked", "NullableProblems"})
        @Override
        public Optional<Repository> load(Dataset key) throws Exception {
            for (Repository<Map<String, Object>> repository : repositories) {
                if (repository.appliesTo(key)) {
                    return Optional.of(repository);
                }
            }
            logger.warn("Could not route dataset:{} because there is no repository configured for this dataset!", key);
            return Optional.empty();
        }
    }
}