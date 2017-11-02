package org.glitch.dragoman.web.subscription;

import org.junit.Test;

import static org.glitch.dragoman.web.subscription.SubscriptionEvent.SubscriptionEventType.STREAM_FAILED_EVENT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubscriptionStreamFailedEventTest {

    @Test
    public void canGetEventType() {
        SubscriptionStreamFailedEvent streamEvent = new SubscriptionStreamFailedEvent("aKey", "boom!");

        assertThat(streamEvent.isTerminal(), is(true));
        assertThat(streamEvent.is(STREAM_FAILED_EVENT), is(true));
    }
}