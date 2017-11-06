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
package org.glytching.dragoman;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import org.glytching.dragoman.configuration.guice.AppModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME;

/**
 * The ignition class for this application. Since {@code Vert.x} is our application framework and {@code Guice} is our
 * DI mechanism this class is very much aware of those technologies.
 */
public class Dragoman {
    private static final Logger logger = LoggerFactory.getLogger(Dragoman.class);

    @Inject
    private Vertx vertx;
    @Inject
    private DeploymentOptions deploymentOptions;
    @Inject
    private DragomanVerticle dragomanVerticle;

    public static void main(String[] args) {
        // establish the bridge from vert.x JUL -> SLF4J -> Logback
        System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());

        new Dragoman().start();
    }

    /**
     * Instances the Guice injector, passing in the main configuration module.
     */
    private Dragoman() {
        Injector injector = Guice.createInjector(new AppModule());
        injector.injectMembers(this);
    }

    /**
     * Deploys the main verticle.
     */
    private void start() {
        vertx.deployVerticle(dragomanVerticle, deploymentOptions, stringAsyncResult ->
                logger.info("Started application: {}", stringAsyncResult.result()));
    }
}
