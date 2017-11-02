package org.glitch.dragoman.reader;

import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.repository.Repository;
import org.glitch.dragoman.repository.router.RepositoryRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.Observable;

import java.util.List;
import java.util.Map;

import static org.glitch.dragoman.util.TestFixture.anyDataset;
import static org.glitch.dragoman.util.TestFixture.anyMap;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class MongoReaderTest {

    @Mock
    private RepositoryRouter repositoryRouter;
    @Mock
    private Repository<Map<String, Object>> repository;

    private Reader reader;

    private Dataset dataset;

    private final String select = "aSelect";
    private final String where = "aWhere";
    private final String orderBy = "anOrderBy";

    @Before
    public void setUp() {
        dataset = anyDataset();

        when(repositoryRouter.get(dataset)).thenReturn(repository);

        reader = new ReaderImpl(repositoryRouter);
    }

    @Test
    public void canRead() {
        Map<String, Object> one = anyMap();
        Map<String, Object> two = anyMap();

        Observable<Map<String, Object>> response = Observable.just(one, two);

        when(repository.find(dataset, select, where, orderBy, -1)).thenReturn(response);

        List<DataEnvelope> dataEnvelopes = reader.read(dataset, select, where, orderBy, -1)
                .toList()
                .toBlocking()
                .single();

        assertThat(dataEnvelopes.size(), is(2));
        assertThat(dataEnvelopes, hasItem(new DataEnvelope(dataset.getSource(), one)));
        assertThat(dataEnvelopes, hasItem(new DataEnvelope(dataset.getSource(), two)));
    }
}