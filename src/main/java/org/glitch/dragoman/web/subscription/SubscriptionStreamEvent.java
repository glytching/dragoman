package org.glitch.dragoman.web.subscription;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SubscriptionStreamEvent<T> extends SubscriptionEvent {

    private final T payload;

    public SubscriptionStreamEvent(String subscriptionKey, T payload) {
        super(SubscriptionEventType.STREAM_EVENT, subscriptionKey);
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
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
