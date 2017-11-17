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
package io.github.glytching.dragoman.web.subscription;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

public class SubscriptionStreamFailedEvent extends SubscriptionEvent {

  private final String failureMessage;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<Throwable> throwable;

  public SubscriptionStreamFailedEvent(String subscriptionKey, String failureMessage) {
    this(subscriptionKey, failureMessage, Optional.empty());
  }

  public SubscriptionStreamFailedEvent(
      String subscriptionKey, String failureMessage, Throwable throwable) {
    this(subscriptionKey, failureMessage, Optional.of(throwable));
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private SubscriptionStreamFailedEvent(
      String subscriptionKey, String failureMessage, Optional<Throwable> throwable) {
    super(SubscriptionEventType.STREAM_FAILED_EVENT, subscriptionKey);
    this.failureMessage = failureMessage;
    this.throwable = throwable;
  }

  public String getFailureMessage() {
    return failureMessage;
  }

  public Optional<Throwable> getThrowable() {
    return throwable;
  }

  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
