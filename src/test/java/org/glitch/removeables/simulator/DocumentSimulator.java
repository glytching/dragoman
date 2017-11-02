package org.glitch.removeables.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.UnicastSubject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocumentSimulator implements Simulator<Document> {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSimulator.class);

    private final DocumentSimulatedEventFactory simulatedEventFactory;
    private final SimulatorDelay simulatorDelay;
    private final ObjectMapper mapper;

    public DocumentSimulator(SimulatorDelay simulatorDelay, DocumentSimulatedEventFactory simulatedEventFactory) {
        this.simulatorDelay = simulatorDelay;
        this.simulatedEventFactory = simulatedEventFactory;
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public Observable<Document> simulate(int simulationCount) {
        logger.debug("Creating simulated observable with size: {}", simulationCount);
        UnicastSubject<Document> observable = UnicastSubject.create(simulationCount);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        observable.observeOn(Schedulers.from(executor));

        executor.submit(() -> {
            for (int i = 0; i < simulationCount; i++) {
                logger.debug("Pushing event {} into the observable", i);

                observable.onNext(simulatedEventFactory.create());

                simulatorDelay.delay(i);
            }
            logger.info("Finished pushing events into the observable");
            observable.onCompleted();
        });
        executor.shutdown();

        logger.info("Returning simulated observable");
        return observable;
    }

    @Override
    public Observable<Document> emit(int simulationCount) {
        return null;
    }

    @Override
    public void record(int simulationCount) {

    }
}