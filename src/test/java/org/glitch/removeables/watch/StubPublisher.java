package org.glitch.removeables.watch;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StubPublisher implements Publisher<EventWrapper> {
    private static final Logger logger = LoggerFactory.getLogger(StubPublisher.class);

    private final List<EventWrapper> received = Lists.newArrayList();

    @Override
    public void publish(EventWrapper publishable) {
        logger.info("Received: {}", publishable);
        received.add(publishable);
    }

    @Override
    public void publish(Throwable throwable) {
        logger.info("Failed: {}", throwable.getMessage());
    }

    boolean contains(EventWrapper eventWrapper) {
        return received.contains(eventWrapper);
    }
}
