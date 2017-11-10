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
package org.glytching.dragoman.web.subscription;

import org.glytching.dragoman.dataset.Dataset;

import static java.lang.String.format;

/**
 * Extends {@link RuntimeException} to provide some identity and traceability for exceptions which arise from our
 * subscription layer.
 */
public class SubscriptionUnsupportedException extends RuntimeException {

    public static SubscriptionUnsupportedException createUnsubscribable(Dataset dataset) {
        return new SubscriptionUnsupportedException(
                format("The dataset: %s is not subscribable, it has no subscriptionControlField!", dataset.getName())
        );
    }

    public static SubscriptionUnsupportedException createConcurrentSubscription(Dataset dataset) {
        return new SubscriptionUnsupportedException(
                format("The dataset: %s already has an active subscription, you cannot have more than one subscription " +
                        "to a given dataset at any given time!", dataset.getName())
        );
    }

    private SubscriptionUnsupportedException(String message) {
        super(message);
    }
}
