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
package io.github.glytching.dragoman.util;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A simple (crude, even) stop watch implementation. Yes, there are plenty of existing stop watch
 * implementations out there but we need lap/split features.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class StopWatch {

    private final org.apache.commons.lang3.time.StopWatch main;
    private Optional<org.apache.commons.lang3.time.StopWatch> split;

    private StopWatch(boolean withSplits) {
        this.main = new org.apache.commons.lang3.time.StopWatch();
        main.start();
        if (withSplits) {
            this.split = Optional.of(new org.apache.commons.lang3.time.StopWatch());
            split.get().start();
        }
    }

    /**
     * Create and start a {@link StopWatch} instance.
     *
     * @return a {@link StopWatch} instance which has already been started
     */
    public static StopWatch start() {
        return new StopWatch(false);
    }

    /**
     * Create and start a {@link StopWatch} instance which can be used to gather split/lap times.
     *
     * @return a {@link StopWatch} instance which has already been started
     */
    public static StopWatch startForSplits() {
        return new StopWatch(true);
    }

    /**
     * Create a split. Note: this will throw an exception if called on a {@link StopWatch} instance
     * which was not started with {@link #startForSplits()}.
     *
     * @return the time since this watch was last split or the time since this watch was started if
     * this is the first split
     */
    public long split() {
        if (split == null || !split.isPresent()) {
            throw new IllegalStateException(
                    "You cannot split a StopWatch which has not been configured withSplits=true!");
        }

        split.get().stop();
        long time = split.get().getTime(TimeUnit.MILLISECONDS);
        split.get().reset();
        split.get().start();
        return time;
    }

    /**
     * Stop this watch.
     *
     * @return the time since this watch was started or if this watch was created for splits the time
     * since the last split
     */
    public long stop() {
        main.stop();
        if (split != null) {
            split.get().stop();
        }
        return main.getTime(TimeUnit.MILLISECONDS);
    }
}
