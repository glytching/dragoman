package org.glitch.dragoman.web.subscription;

public class SubscriptionContext {

    private final long timerId;
    private final AsOf asOf;

    public SubscriptionContext(long timerId, AsOf asOf) {
        this.timerId = timerId;
        this.asOf = asOf;
    }

    public long getTimerId() {
        return timerId;
    }

    public AsOf getAsOf() {
        return asOf;
    }
}
