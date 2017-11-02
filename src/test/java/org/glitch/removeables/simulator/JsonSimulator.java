package org.glitch.removeables.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subjects.UnicastSubject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonSimulator implements Simulator<String> {
    private static final Logger logger = LoggerFactory.getLogger(JsonSimulator.class);

    private final SimulatedEventFactory<String> simulatedEventFactory;
    private final SimulatorDelay simulatorDelay;
    private ReplaySubject<String> recordedObservable;

    @SuppressWarnings("unchecked")
    JsonSimulator(SimulatorDelay simulatorDelay, SimulatedEventFactory simulatedEventFactory) {
        this.simulatorDelay = simulatorDelay;
        this.simulatedEventFactory = simulatedEventFactory;
    }

    public Observable<String> simulate(int simulationCount) {
        logger.info("Creating observable");
        UnicastSubject<String> observable = UnicastSubject.create(simulationCount);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        observable.observeOn(Schedulers.from(executor));

        executor.submit(() -> {
            for (int i = 0; i < simulationCount; i++) {
                logger.info("Pushing event {} into the observable", i);

                observable.onNext(simulatedEventFactory.create());

                simulatorDelay.delay(i);
            }
            logger.info("Completed");
            observable.onCompleted();
        });
        executor.shutdown();

        logger.info("Returning observable");
        return observable;
    }

    public Observable<String> emit(int simulationCount) {
        //        logger.info("Creating observable");
        //        Observable<String> observable = Observable.from(emitter -> {
        //            for (int i = 0; i < simulationCount; i++) {
        //                logger.info("Pushing event {} into the observable", i);
        //
        //                emitter.onNext(simulatedEventFactory.create());
        //
        //                simulatorDelay.delay(i);
        //            }
        //            emitter.onCompleted();
        //        }, Emitter.BackpressureMode.NONE);
        //
        //        logger.info("Returning observable");
        //        // onBackpressureBuffer isn't really required
        //        return observable.onBackpressureBuffer();

        return Observable.just("");
    }

    public void record(int simulationCount) {
        logger.info("Recording observable");
        recordedObservable = ReplaySubject.create(simulationCount);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        recordedObservable.observeOn(Schedulers.from(executor));

        executor.submit(() -> {
            for (int i = 0; i < simulationCount; i++) {
                logger.info("Pushing event {} into the observable", i);

                recordedObservable.onNext(simulatedEventFactory.create());

                simulatorDelay.delay(i);
            }
            logger.info("Recording finished");
            recordedObservable.onCompleted();
        });
        executor.shutdown();
    }
}