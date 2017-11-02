package org.glitch.dragoman.web.subscription;

import org.glitch.dragoman.dataset.Dataset;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SubscriptionManager {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void start(Dataset dataset, Optional<Long> refreshPeriodInMillis, LocalDateTime startTime, String select,
               String where);

    void stop(String subscriptionKey);
}
