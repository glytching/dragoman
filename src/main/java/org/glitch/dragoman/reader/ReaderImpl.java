package org.glitch.dragoman.reader;

import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.repository.router.RepositoryRouter;
import rx.Observable;

import javax.inject.Inject;

public class ReaderImpl implements Reader {

    private final RepositoryRouter repositoryRouter;

    @Inject
    public ReaderImpl(RepositoryRouter repositoryRouter) {
        this.repositoryRouter = repositoryRouter;
    }

    @Override
    public Observable<DataEnvelope> read(Dataset dataset, String select, String where, String orderBy,
                                         Integer maxResults) {
        return repositoryRouter.get(dataset).find(dataset, select, where, orderBy, maxResults)
                .map(incoming -> new DataEnvelope(dataset.getSource(), incoming));
    }
}