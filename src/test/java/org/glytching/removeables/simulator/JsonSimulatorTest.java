package org.glytching.removeables.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jayway.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observables.BlockingObservable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@Ignore
public class JsonSimulatorTest {
    private static final Logger logger = LoggerFactory.getLogger(JsonSimulatorTest.class);

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
    @SuppressWarnings("unchecked")
    public void canSimulate() {
        int simulationCount = 5;
        BlockingObservable<String> simulated = simulator.simulate(simulationCount).toBlocking();

        List<Map<String, Object>> received = Lists.newArrayList();
        List<Throwable> failures = Lists.newArrayList();

        logger.info("Subscribing ...");
        simulated.subscribe(
                event -> {
                    try {
                        logger.info("Received: {}", event);
                        received.add(objectMapper.readValue(event, Map.class));
                    } catch (Exception ex) {
                        failures.add(new RuntimeException(format("Failed to deserialise: %s", event), ex));
                    }
                },
                failures::add,
                () -> logger.info("Completed"));

        assertThat(received, hasSize(simulationCount));
        assertThat(format("Expected no failures but got: %s", Joiner.on(",").join(failures)), failures, hasSize(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canSimulateWithAwaitility() {
        int simulationCount = 5;
        Observable<String> simulated = simulator.simulate(simulationCount);

        List<Map<String, Object>> received = Lists.newArrayList();
        List<Throwable> failures = Lists.newArrayList();
        AtomicBoolean completed = new AtomicBoolean(false);
        logger.info("Subscribing ...");
        simulated.subscribe(
                event -> {
                    try {
                        logger.info("Received: {}", event);
                        received.add(objectMapper.readValue(event, Map.class));
                    } catch (Exception ex) {
                        failures.add(new RuntimeException(format("Failed to deserialise: %s", event), ex));
                    }
                },
                failures::add,
                () -> {
                    logger.info("Completed");
                    completed.set(true);
                });

        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).until(completed::get);
        assertThat(received, hasSize(simulationCount));
        assertThat(format("Expected no failures but got: %s", Joiner.on(",").join(failures)), failures, hasSize(0));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void canEmit() {
        int simulationCount = 5;
        // TODO no need to block! why?!
        Observable<String> simulated = simulator.emit(simulationCount);

        List<Map<String, Object>> received = Lists.newArrayList();
        List<Throwable> failures = Lists.newArrayList();

        logger.info("Subscribing ...");
        simulated.subscribe(
                event -> {
                    try {
                        logger.info("Received: {}", event);
                        received.add(objectMapper.readValue(event, Map.class));
                    } catch (Exception ex) {
                        failures.add(new RuntimeException(format("Failed to deserialise: %s", event), ex));
                    }
                },
                failures::add,
                () -> logger.info("Completed"));

        // TODO no need to await! why?!

        assertThat(received, hasSize(simulationCount));
        assertThat(format("Expected no failures but got: %s", Joiner.on(",").join(failures)), failures, hasSize(0));
    }
}