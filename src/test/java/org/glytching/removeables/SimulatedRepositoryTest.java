package org.glytching.removeables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.awaitility.Awaitility;
import groovy.lang.GroovyClassLoader;
import org.glytching.dragoman.ql.listener.groovy.Filter;
import org.glytching.dragoman.ql.listener.groovy.GroovyFactory;
import org.glytching.dragoman.ql.listener.groovy.Mapper;
import org.glytching.dragoman.ql.parser.SelectClauseParser;
import org.glytching.dragoman.ql.parser.WhereClauseParser;
import org.glytching.removeables.simulator.EveryNElementSimulatorDelay;
import org.glytching.removeables.simulator.JsonSimulator;
import org.glytching.removeables.simulator.RandomSimulatedEventFactory;
import org.glytching.removeables.simulator.SimulatorBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Ignore
public class SimulatedRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(SimulatedRepositoryTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonSimulator simulator;

    @Before
    public void setUp() {
        simulator = new SimulatorBuilder()
                .withDelay(new EveryNElementSimulatorDelay(2, 50))
                .withEventCreator(new RandomSimulatedEventFactory())
                .buildJsonSimulator();
    }

    @Test
    public void canSimulateWithMapAndFilter() throws Exception {
        GroovyFactory groovyFactory = new GroovyFactory(new GroovyClassLoader(), new SelectClauseParser(), new WhereClauseParser());

        Mapper mapper = groovyFactory.createProjector("age");
        Filter filter = groovyFactory.createFilter("age > 60");
        int simulationCount = 5;
        Observable<String> simulated = simulator.simulate(simulationCount);

        AtomicBoolean completed = new AtomicBoolean(false);

        logger.info("Subscribing ...");
        simulated.map(toMap())
                .filter(filter::filter)
                .map(mapper::map)
                .subscribe(
                        this::logReceipt,
                        this::logError,
                        () -> {
                            completed.set(true);
                            logCompletion();
                        }
                );

        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).until(completed::get);
    }

    private void logReceipt(Map<String, Object> event) {
        logger.info("Received: {}", event);
    }

    private void logError(Throwable throwable) {
        logger.error("Failed to read!", throwable);
    }

    private void logCompletion() {
        logger.info("Completed!");
    }

    @SuppressWarnings("unchecked")
    private Func1<String, Map<String, Object>> toMap() {
        return s -> {
            try {
                // transform to Map<String, Object>
                return objectMapper.readValue(s, Map.class);
            } catch (IOException e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e);
            }
        };
    }
}