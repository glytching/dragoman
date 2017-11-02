package org.glitch.dragoman.web.subscription;

public abstract class SubscriptionEvent {

    public enum SubscriptionEventType {
        STREAM_EVENT, STREAM_FAILED_EVENT, STREAM_COMPLETED_EVENT;

        public boolean isTerminal() {
            return this == STREAM_COMPLETED_EVENT || this == STREAM_FAILED_EVENT;
        }
    }

    private SubscriptionEventType eventType;
    private String subscriptionKey;

    public SubscriptionEvent() {
    }

    public SubscriptionEvent(SubscriptionEventType eventType, String subscriptionKey) {
        this.eventType = eventType;
        this.subscriptionKey = subscriptionKey;
    }

    public SubscriptionEventType getEventType() {
        return eventType;
    }

    public void setEventType(SubscriptionEventType eventType) {
        this.eventType = eventType;
    }

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public void setSubscriptionKey(String subscriptionKey) {
        this.subscriptionKey = subscriptionKey;
    }

    public boolean is(SubscriptionEventType incoming) {
        return incoming == eventType;
    }

    public boolean isTerminal() {
        return eventType == SubscriptionEventType.STREAM_FAILED_EVENT ||
                eventType == SubscriptionEventType.STREAM_COMPLETED_EVENT;
    }
}
