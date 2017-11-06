package org.glytching.removeables.rx;

import org.bson.Document;
import rx.Observer;
import rx.observers.TestSubscriber;

/**
 * Subscriber helper implementations for the Quick Tour.
 */
public final class SubscribersHelper {

    /**
     * A Subscriber that prints each result onNext
     *
     * @param <T> The TestSubscriber result type
     *
     * @return the subscriber
     */
    public static <T> TestSubscriber<T> printSubscriber() {
        return printSubscriber(null);
    }

    /**
     * A Subscriber that prints each result onNext
     *
     * @param <T> The TestSubscriber result type
     * @param onStartMessage the initial start message printed on return of the first item.
     *
     * @return the subscriber
     */
    public static <T> TestSubscriber<T> printSubscriber(final String onStartMessage) {
        return new TestSubscriber<>(new Observer<T>() {
            private boolean first = true;

            @Override
            public void onCompleted() {
                System.out.println();
            }

            @Override
            public void onError(final Throwable t) {
                System.out.println("The Observer errored: " + t.getMessage());
            }

            @Override
            public void onNext(final T t) {
                if (first && onStartMessage != null) {
                    System.out.print(onStartMessage);
                    first = false;
                }
                System.out.print(t + " ");
            }
        });
    }

    /**
     * A Subscriber that prints the json version of each document
     *
     * @return the subscriber
     */
    public static TestSubscriber<Document> printDocumentSubscriber() {
        return new TestSubscriber<>(new Observer<Document>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(final Throwable t) {
                System.out.println("The Observer errored: " + t.getMessage());
            }

            @Override
            public void onNext(final Document document) {
                System.out.println(document.toJson());
            }
        });
    }

    private SubscribersHelper() {
    }
}
