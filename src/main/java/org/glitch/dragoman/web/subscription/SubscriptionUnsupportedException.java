package org.glitch.dragoman.web.subscription;

import org.glitch.dragoman.dataset.Dataset;

import static java.lang.String.format;

public class SubscriptionUnsupportedException extends RuntimeException {

    public SubscriptionUnsupportedException(Dataset dataset) {
        super(format("The dataset: %s has is not subscribable, it has no subscriptionControlField!", dataset.getName
                ()));
    }
}
