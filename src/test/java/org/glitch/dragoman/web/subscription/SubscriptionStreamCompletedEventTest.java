package org.glitch.dragoman.web.subscription;

import org.junit.Test;

import static org.glitch.dragoman.web.subscription.SubscriptionEvent.SubscriptionEventType.STREAM_COMPLETED_EVENT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubscriptionStreamCompletedEventTest {

    @Test
    public void canGetEventType() {
        SubscriptionStreamCompletedEvent streamEvent = new SubscriptionStreamCompletedEvent("aKey");

        assertThat(streamEvent.isTerminal(), is(true));
        assertThat(streamEvent.is(STREAM_COMPLETED_EVENT), is(true));
    }
}