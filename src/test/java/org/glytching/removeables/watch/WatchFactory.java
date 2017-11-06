package org.glytching.removeables.watch;

import rx.Observable;

public class WatchFactory {
    private final DynamicInspectorFactory dynamicInspectorFactory;
    private final Publisher<EventWrapper> publisher;

    WatchFactory(DynamicInspectorFactory dynamicInspectorFactory, Publisher<EventWrapper> publisher) {
        this.dynamicInspectorFactory = dynamicInspectorFactory;
        this.publisher = publisher;
    }

    public Watch create(Observable<EventWrapper> observable, String selectClause, String whereClause, String source) {
        return new Watch(observable, publisher, dynamicInspectorFactory.createProjector(selectClause),
                dynamicInspectorFactory.createFilter(whereClause), source);
    }
}
