/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glytching.dragoman.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void cannotTimeWithSplitsIfTheStopWatchIsNotConfiguredToSpli() {
        StopWatch stopWatch = StopWatch.start();

        assertThrows(IllegalStateException.class, () -> stopWatch.split());
    }

    private void sleep(int pauseInMs) {
        try {
            Thread.sleep(pauseInMs);
        } catch (InterruptedException ex) {
            logger.warn("Failed to pause!", ex);
        }
    }
}
