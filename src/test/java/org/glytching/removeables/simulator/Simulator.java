package org.glytching.removeables.simulator;

import rx.Observable;

public interface Simulator<T> {

    Observable<T> simulate(int simulationCount);

    Observable<T> emit(int simulationCount);

    void record(int simulationCount);
}