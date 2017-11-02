package org.glitch.dragoman.store.mongo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.store.mongo.DocumentTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.Observable;

import java.util.List;
import java.util.Map;

import static org.glitch.dragoman.util.TestFixture.anyDataset;
import static org.glitch.dragoman.util.TestFixture.anyDocument;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class DecoratingMongoRepositoryTest {

    @Mock
    private MongoRepository delegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String select = "aSelect";
    private final String where = "aWhere";
    private final String orderBy = "nOrderBy";
    private final int maxResults = 50;

    private Dataset dataset;
    private DocumentTransformer documentTransformer;
    private DecoratingMongoRepository repository;

    @Before
    public void setUp() {
        dataset = anyDataset();

        documentTransformer = new DocumentTransformer(objectMapper);

        repository = new DecoratingMongoRepository(delegate, documentTransformer);
    }

    @Test
    public void appliesToWhateverTheDelegateAppliesTo() {
        Dataset dataset = anyDataset();

        when(delegate.appliesTo(dataset)).thenReturn(true);
        assertThat(repository.appliesTo(dataset), is(true));

        when(delegate.appliesTo(dataset)).thenReturn(false);
        assertThat(repository.appliesTo(dataset), is(false));
    }

    @Test
    public void willDelegateThenTransformTheResponse() {
        Document one = anyDocument();
        Document two = anyDocument();

        when(delegate.find(dataset, select, where, orderBy, maxResults)).thenReturn(Observable.just(one, two));

        List<Map<String, Object>> results = toList(repository.find(dataset, select, where, orderBy, maxResults));

        assertThat(results.size(), is(2));
        assertThat(results, hasItem(documentTransformer.transform(one)));
        assertThat(results, hasItem(documentTransformer.transform(two)));
    }

    private List<Map<String, Object>> toList(Observable<Map<String, Object>> observable) {
        return observable.toList().toBlocking().single();
    }

}
