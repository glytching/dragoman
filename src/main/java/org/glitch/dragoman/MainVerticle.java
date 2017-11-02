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
package org.glitch.dragoman;

import com.google.inject.Inject;
import io.vertx.core.*;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.store.mongo.EmbeddedMongoVerticle;
import org.glitch.dragoman.web.WebServerVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    private List<String> deploymentIds;

    private final WebServerVerticle webServerVerticle;
    private final EmbeddedMongoVerticle embeddedMongoVerticle;
    private final DeploymentOptions deploymentOptions;
    private final ApplicationConfiguration configuration;

    @Inject
    public MainVerticle(WebServerVerticle webServerVerticle, EmbeddedMongoVerticle embeddedMongoVerticle,
                        DeploymentOptions deploymentOptions, ApplicationConfiguration configuration) {
        this.webServerVerticle = webServerVerticle;
        this.embeddedMongoVerticle = embeddedMongoVerticle;
        this.deploymentOptions = deploymentOptions;
        this.configuration = configuration;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        deploymentIds = new ArrayList<>(3);
    }

    @Override
    public void start(Future<Void> future) {
        CompositeFuture
                .all(deployEmbeddedMongo(), deployWebServer())
                .setHandler(future.<CompositeFuture>map(c -> null).completer());
    }

    @Override
    public void stop(Future<Void> future) {
        CompositeFuture.all(
                deploymentIds
                        .stream()
                        .map(this::undeploy)
                        .collect(Collectors.toList())
        ).setHandler(future.<CompositeFuture>map(c -> null).completer());
    }

    private Future<String> deployEmbeddedMongo() {
        Future<String> future = Future.future();
        if (configuration.isMongoEmbedded()) {
            DeploymentOptions options = new DeploymentOptions();
            options.setWorker(true);
            vertx.deployVerticle(embeddedMongoVerticle, options, resultHandler -> {
                if (resultHandler.failed()) {
                    logger.warn("Failed to deploy EmbeddedMongoVerticle", resultHandler.cause());
                    future.fail(resultHandler.cause());
                } else {
                    String deploymentId = resultHandler.result();
                    logger.info("Deployed EmbeddedMongoVerticle verticle with id: {}", deploymentId);
                    deploymentIds.add(deploymentId);
                    future.complete();
                }
            });
        } else {
            future.complete();
        }
        return future;
    }

    private Future<String> deployWebServer() {
        Future<String> future = Future.future();
        vertx.deployVerticle(webServerVerticle, deploymentOptions, resultHandler -> {
            if (resultHandler.failed()) {
                logger.warn("Failed to deploy WebServerVerticle", resultHandler.cause());
                future.fail(resultHandler.cause());
            } else {
                String deploymentId = resultHandler.result();
                logger.info("Deployed WebServerVerticle verticle with id: {}", deploymentId);
                deploymentIds.add(deploymentId);
                future.complete();
            }
        });
        return future;
    }

    private Future<Void> undeploy(String deploymentId) {
        logger.info("Undeploying a verticle with deploymentId: {}", deploymentId);

        Future<Void> future = Future.future();
        vertx.undeploy(deploymentId, res -> {
            if (res.succeeded()) {
                future.complete();
            } else {
                future.fail(res.cause());
            }
        });
        return future;
    }
}
