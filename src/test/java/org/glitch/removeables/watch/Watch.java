package org.glitch.removeables.watch;

import org.glitch.dragoman.ql.listener.groovy.Filter;
import org.glitch.dragoman.ql.listener.groovy.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.lang.String.format;

public class Watch {
    private static final Logger logger = LoggerFactory.getLogger(Watch.class);

    private final Observable<EventWrapper> observable;
    private final Publisher<EventWrapper> publisher;
    private final Mapper mapper;
    private final Filter filter;
    private final String source;
    private Subscription subscription;

    Watch(Observable<EventWrapper> observable, Publisher<EventWrapper> publisher, Mapper mapper,
          Filter filter, String source) {
        this.observable = observable;
        this.publisher = publisher;
        this.mapper = mapper;
        this.filter = filter;
        this.source = source;
    }

    public void start() {
        this.subscription = observable.filter(this::matchesSource)
                .observeOn(Schedulers.immediate())
                //                .observeOn(Schedulers.io())
                .concatMap(event -> Observable.defer(() -> Observable.just(event)
                        .filter(this::applyFilter)
                        .map(this::applyProjection))
                        .onErrorResumeNext(logAndSkip(event)))
                .subscribe(this::publish,
                        this::logUnrecoverableError,
                        this::logWatchCompleted);
    }

    public void stop() {
        this.subscription.unsubscribe();
    }

    private void logWatchCompleted() {
        logger.info("Completed watch!");
    }

    private void logUnrecoverableError(Throwable throwable) {
        logger.error("Unrecoverable error for watch!", throwable);

        // push the failed event back to the client
        publisher.publish(throwable);
    }

    private void publish(EventWrapper eventWrapper) {
        // log it

        // push the successful event back to the client
        publisher.publish(eventWrapper);
    }

    private Func1<Throwable, Observable<EventWrapper>> logAndSkip(EventWrapper eventWrapper) {
        return t -> {
            logger.error(format("Error handling event: %s", eventWrapper), t);
            return Observable.empty();
        };
    }

    private EventWrapper applyProjection(EventWrapper eventWrapper) {
        // TODO: this is ugly, no need for new here just mutate the incoming instance!
        // or else return a new type here e.g. a PublishableEnvelope?
        return new EventWrapper(source, mapper.map(eventWrapper.getEvent()));
    }

    private boolean applyFilter(EventWrapper eventWrapper) {
        return filter.filter(eventWrapper.getEvent());
    }

    private boolean matchesSource(EventWrapper eventWrapper) {
        return source.equals(eventWrapper.getSource());
    }
}
