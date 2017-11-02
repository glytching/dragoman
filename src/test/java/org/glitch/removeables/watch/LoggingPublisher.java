package org.glitch.removeables.watch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingPublisher implements Publisher<EventWrapper> {
    private static final Logger logger = LoggerFactory.getLogger(LoggingPublisher.class);

    @Override
    public void publish(EventWrapper publishable) {
        logger.info("Received: {}", publishable);
    }

    @Override
    public void publish(Throwable throwable) {
        logger.info("Failed: {}", throwable.getMessage());
    }
}