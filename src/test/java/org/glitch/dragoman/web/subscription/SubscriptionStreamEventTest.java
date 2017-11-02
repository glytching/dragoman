package org.glitch.dragoman.web.subscription;

import org.junit.Test;

import static org.glitch.dragoman.web.subscription.SubscriptionEvent.SubscriptionEventType.STREAM_EVENT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubscriptionStreamEventTest {

    @Test
    public void canGetEventType() {
        SubscriptionStreamEvent<String> streamEvent = new SubscriptionStreamEvent<>("aKey", "aPayload");

        assertThat(streamEvent.isTerminal(), is(false));
        assertThat(streamEvent.is(STREAM_EVENT), is(true));
    }
}