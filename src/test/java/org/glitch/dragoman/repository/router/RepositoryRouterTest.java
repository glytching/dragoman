package org.glitch.dragoman.repository.router;

import com.google.common.collect.Sets;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.repository.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.glitch.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

// we walk over the router's injected repositories and return-on-first-match but in our test case we have to be certain
// that only a specific repository is matched so we set expectations on all injected repositories
// of course, since we return-on-first-match some of these repositories may never be checked
// this causes Mockito to issue a warning which is (in this case) unnecessary, the Silent mode squashes that behaviour
@RunWith(MockitoJUnitRunner.Silent.class)
public class RepositoryRouterTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Repository<Map<String, Object>> repositoryA;
    @Mock
    private Repository<Map<String, Object>> repositoryB;

    private RepositoryRouter router;

    @Before
    public void setUp() {
        router = new RepositoryRouterImpl(Sets.newHashSet(repositoryA, repositoryB));
    }

    @Test
    public void canRouteADatasetToARepository() {
        Dataset dataset = anyDataset();
        when(repositoryA.appliesTo(dataset)).thenReturn(true);
        when(repositoryB.appliesTo(dataset)).thenReturn(false);

        Repository<Map<String, Object>> actual = router.get(dataset);

        assertThat(actual, is(repositoryA));
    }

    @Test
    public void willFailIfNoRepositoryExistsForTheGivenDataset() {
        Dataset dataset = anyDataset();

        expectedException.expect(NoRepositoryAvailableException.class);
        expectedException.expectMessage(is("No repository exists for dataset: " + dataset.getId()));

        when(repositoryA.appliesTo(dataset)).thenReturn(false);
        when(repositoryB.appliesTo(dataset)).thenReturn(false);

        router.get(dataset);
    }
}
