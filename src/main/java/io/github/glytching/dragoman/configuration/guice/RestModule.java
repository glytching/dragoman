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
package io.github.glytching.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import io.github.glytching.dragoman.web.resource.*;
import io.github.glytching.dragoman.web.subscription.SubscriptionManager;
import io.github.glytching.dragoman.web.subscription.VertxSubscriptionManager;

public class RestModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder<RestResource> multibinder = Multibinder.newSetBinder(binder(), RestResource.class);
    multibinder.addBinding().to(PingResource.class);
    multibinder.addBinding().to(MetricsResource.class);
    multibinder.addBinding().to(HealthCheckResource.class);
    multibinder.addBinding().to(DatasetResource.class);
    multibinder.addBinding().to(AuthenticationResource.class);

    bind(SubscriptionManager.class).to(VertxSubscriptionManager.class);
  }
}
