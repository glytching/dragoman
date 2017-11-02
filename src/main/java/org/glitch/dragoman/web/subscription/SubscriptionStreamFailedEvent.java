package org.glitch.dragoman.web.subscription;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

public class SubscriptionStreamFailedEvent extends SubscriptionEvent {

    private final String failureMessage;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Throwable> throwable;

    public SubscriptionStreamFailedEvent(String subscriptionKey, String failureMessage) {
        this(subscriptionKey, failureMessage, Optional.empty());
    }

    public SubscriptionStreamFailedEvent(String subscriptionKey, String failureMessage, Throwable throwable) {
        this(subscriptionKey, failureMessage, Optional.of(throwable));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private SubscriptionStreamFailedEvent(String subscriptionKey, String failureMessage,
                                          Optional<Throwable> throwable) {
        super(SubscriptionEventType.STREAM_FAILED_EVENT, subscriptionKey);
        this.failureMessage = failureMessage;
        this.throwable = throwable;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public Optional<Throwable> getThrowable() {
        return throwable;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
