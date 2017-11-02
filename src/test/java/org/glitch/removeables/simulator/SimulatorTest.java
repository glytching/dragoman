package org.glitch.removeables.simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@Ignore
public class SimulatorTest {
    private static final Logger logger = LoggerFactory.getLogger(SimulatorTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    // generate JSON data streams
    // have to be streams to simulate how we will read dta from (a) the db and (b) external REST services
    // we don't care about structure, will provide templates/population-strategies later
    // start with simple, single level, key/value pairs


    @Test
    @SuppressWarnings("unchecked")
    public void testSimulate() throws InterruptedException {
        int simulationCount = 5;
        Observable<String> simulated = simulate(simulationCount);

        // shows how to use a wrapper to handle exceptions in the map function
        simulated.map(RxMapWrapper.wrapAndThrow(document -> document));

        List<Map<String, Object>> received = Lists.newArrayList();
        List<Throwable> failures = Lists.newArrayList();

        logger.info("Subscribing ...");
        simulated.subscribe(
                event -> {
                    try {
                        received.add(objectMapper.readValue(event, Map.class));
                    } catch (IOException ex) {
                        failures.add(new RuntimeException(format("Failed to deserialise: %s", event), ex));
                    }
                },
                failures::add,
                () -> logger.info("Completed"));

        assertThat(received, hasSize(simulationCount));
        assertThat(format("Expected no failures but got: %s", Joiner.on(",").join(failures)), failures, hasSize(0));
    }

    private Observable<String> simulate(int count) {
        logger.info("Creating observable");
        ReplaySubject<String> observable = ReplaySubject.create();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        observable.observeOn(Schedulers.from(executor));

        executor.submit(() -> {
            for (int i = 0; i < count; i++) {
                logger.info("Pushing {}", i);
                Map<String, Object> doc = Maps.newHashMap();
                doc.put("key", "" + i);
                try {
                    observable.onNext(objectMapper.writeValueAsString(doc));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Completed");
            observable.onCompleted();
        });

        logger.info("Returning observable");
        return observable;
    }

    @Test
    public void testSimulateA() throws InterruptedException {

        ObservableList<String> simulated = simulateA(50);

        simulated.getObservable().subscribe(System.out::println);


    }

    private ObservableList<String> simulateA(int count) {
        ObservableList<String> olist = new ObservableList<>();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        olist.getObservable().observeOn(Schedulers.from(executor));

        executor.submit(() -> {
            for (int i = 0; i < count; i++) {
                logger.info("Pushing {}", i);
                Map<String, Object> doc = Maps.newHashMap();
                doc.put("key", "" + i);
                try {
                    olist.add(objectMapper.writeValueAsString(doc));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdown();


        return olist;
    }

    public static class ObservableList<T> {

        final List<T> list;
        final ReplaySubject<T> onAdd;

        public ObservableList() {
            this.list = new ArrayList<>();
            this.onAdd = ReplaySubject.create();
        }

        void add(T value) {
            list.add(value);
            onAdd.onNext(value);
        }

        public Observable<T> getObservable() {
            return onAdd;
        }
    }
}
