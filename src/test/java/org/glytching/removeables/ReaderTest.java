package org.glytching.removeables;

import com.jayway.awaitility.Awaitility;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.reader.DataEnvelope;
import org.glytching.dragoman.reader.Reader;
import org.glytching.dragoman.reader.ReaderImpl;
import org.glytching.dragoman.repository.Repository;
import org.glytching.dragoman.repository.router.RepositoryRouter;
import org.glytching.removeables.simulator.JsonSimulator;
import org.glytching.removeables.simulator.SimulatorBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class ReaderTest {
    private static final Logger logger = LoggerFactory.getLogger(ReaderTest.class);

    private RepositoryRouter repositoryRouter;
    private Repository<Map<String, Object>> repository;
    private JsonSimulator simulator;

    private Reader reader;

    @Before
    public void setUp() {
        // TODO perhpas one too many layers here, I think a simulator *is* a repository!
        // maybe the correct paradigm is to record something into the simulator and then let it play itself
        simulator = new SimulatorBuilder().buildJsonSimulator();

        repository = new SimulatedRepository(simulator);

        repositoryRouter = mock(RepositoryRouter.class);
        when(repositoryRouter.get(any(Dataset.class))).thenReturn(repository);

        reader = new ReaderImpl(this.repositoryRouter);
    }

    @Test
    public void foo() {
        AtomicBoolean completed = new AtomicBoolean(false);
        reader.read(anyDataset(), "id, name", "age > 55", "", -1).subscribe(
                this::logReceipt,
                this::logError,
                () -> {
                    completed.set(true);
                    logCompletion();
                }
        );

        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).until(completed::get);

    }

    private void logReceipt(DataEnvelope event) {
        logger.info("Received: {}", event);
    }

    private void logError(Throwable throwable) {
        logger.error("Failed to read!", throwable);
    }

    private void logCompletion() {
        logger.info("Completed!");
    }
}