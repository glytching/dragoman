package org.glitch.dragoman.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class StopWatchTest {
    private static final Logger logger = LoggerFactory.getLogger(StopWatchTest.class);

    @Test
    public void canTime() {
        StopWatch stopWatch = StopWatch.start();

        sleep(5);

        long total = stopWatch.stop();

        assertThat(total, greaterThanOrEqualTo(5L));
    }

    @Test
    public void canTimeWithSplits() {
        StopWatch stopWatch = StopWatch.startForSplits();

        sleep(5);
        long splitOne = stopWatch.split();

        sleep(10);
        long splitTwo = stopWatch.split();

        long total = stopWatch.stop();

        assertThat(splitOne, greaterThanOrEqualTo(5L));
        assertThat(splitTwo, greaterThanOrEqualTo(10L));
        assertThat(total, greaterThanOrEqualTo(splitOne + splitTwo));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotTimeWithSplitsIfTheStopWatchIsNotConfiguredToSpli() {
        StopWatch stopWatch = StopWatch.start();

        stopWatch.split();
    }

    private void sleep(int pauseInMs) {
        try {
            Thread.sleep(pauseInMs);
        } catch (InterruptedException ex) {
            logger.warn("Failed to pause!", ex);
        }
    }

}
