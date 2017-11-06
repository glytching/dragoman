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
package org.glytching.dragoman.store.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.event.*;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import org.glytching.dragoman.configuration.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Simple provider implementation wrapping a {@link MongoClient}. Allows us to delay the initialisation of the
 * {@link MongoClient} (which is useful when running in embeddedmode) and also helps with testability.
 */

public class MongoProviderImpl implements MongoProvider {
    private static final Logger logger = LoggerFactory.getLogger(MongoProviderImpl.class);

    private final ApplicationConfiguration applicationConfiguration;
    private MongoClient mongoClient;

    @Inject
    public MongoProviderImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public MongoClient provide() {
        if (mongoClient == null) {
            mongoClient = createMongoClient();
        }

        return mongoClient;
    }

    /**
     * Lazily instantiate the {@link MongoClient} instance.
     *
     * @return
     */
    private MongoClient createMongoClient() {
        String host = applicationConfiguration.getMongoHost();
        int port = applicationConfiguration.getMongoPort();
        ConnectionString connectionString = new ConnectionString("mongodb://" + host + ":" + port);

        logger.info("Creating Mongo client for: {}:{}", host, port);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applicationName("dragoman")
                .serverSettings(ServerSettings.builder()
                        .applyConnectionString(connectionString)
                        .addServerMonitorListener(new LoggingServerMonitorListener())
                        .addServerListener(new LoggingServerListener())
                        .build())
                .clusterSettings(ClusterSettings.builder()
                        .applyConnectionString(connectionString)
                        .serverSelectionTimeout(applicationConfiguration.getMongoServerSelectionTimeout(), MILLISECONDS)
                        .addClusterListener(new LoggingClusterListener())
                        .build())
                .connectionPoolSettings(ConnectionPoolSettings.builder()
                        .applyConnectionString(connectionString)
                        .maxWaitTime(applicationConfiguration.getConnectionPoolMaxWaitTime(), MILLISECONDS)
                        .minSize(applicationConfiguration.getConnectionPoolMinSize())
                        .maxSize(applicationConfiguration.getConnectionPoolMaxSize())
                        .addConnectionPoolListener(new LoggingConnectionPoolListener())
                        .build())
                .socketSettings(SocketSettings.builder()
                        .applyConnectionString(connectionString)
                        .connectTimeout(applicationConfiguration.getMongoSocketConnectionTimeout(), MILLISECONDS)
                        .readTimeout(applicationConfiguration.getMongoReadTimeout(), MILLISECONDS)
                        .build())
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    private class LoggingServerListener implements ServerListener {
        private final Logger logger = LoggerFactory.getLogger(LoggingServerListener.class);

        @Override
        public void serverOpening(ServerOpeningEvent event) {
            logger.debug("serverOpening: {}", event.getServerId().getAddress());
        }

        @Override
        public void serverClosed(ServerClosedEvent event) {
            logger.debug("serverClosed: {}", event.getServerId().getAddress());
        }

        @Override
        public void serverDescriptionChanged(ServerDescriptionChangedEvent event) {
            logger.debug("serverDescriptionChanged: {} from {} to {}", event.getServerId().getAddress(),
                    event.getPreviousDescription(), event.getNewDescription());
        }
    }

    private class LoggingClusterListener implements ClusterListener {
        private final Logger logger = LoggerFactory.getLogger(LoggingClusterListener.class);

        @Override
        public void clusterOpening(ClusterOpeningEvent event) {
            logger.debug("clusterOpening: {}", event.getClusterId().getDescription());
        }

        @Override
        public void clusterClosed(ClusterClosedEvent event) {
            logger.debug("clusterClosed: {}", event.getClusterId().getDescription());
        }

        @Override
        public void clusterDescriptionChanged(ClusterDescriptionChangedEvent event) {
            logger.debug("clusterDescriptionChanged: {} from {} to {}", event.getClusterId().getDescription(),
                    event.getPreviousDescription(), event.getNewDescription());
        }
    }

    private class LoggingServerMonitorListener implements ServerMonitorListener {
        private final Logger logger = LoggerFactory.getLogger(LoggingServerMonitorListener.class);

        @Override
        public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {
            logger.debug("serverHeartbeatStarted: {}", event.getConnectionId().getServerId().getAddress());
        }

        @Override
        public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
            logger.debug("serverHeartbeatSucceeded: {}", event.getConnectionId().getServerId().getAddress());
        }

        @Override
        public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
            logger.debug("serverHeartbeatFailed: {}", event.getConnectionId().getServerId().getAddress());
        }
    }

    private class LoggingConnectionPoolListener implements ConnectionPoolListener {
        private final Logger logger = LoggerFactory.getLogger(LoggingConnectionPoolListener.class);

        @Override
        public void connectionPoolOpened(ConnectionPoolOpenedEvent event) {
            logger.debug("connectionPoolOpened: {}", event.getServerId().getAddress());
        }

        @Override
        public void connectionPoolClosed(ConnectionPoolClosedEvent event) {
            logger.debug("connectionPoolClosed: {}", event.getServerId().getAddress());
        }

        @Override
        public void connectionCheckedOut(ConnectionCheckedOutEvent event) {
            logger.debug("connectionCheckedOut: {}/{}", event.getConnectionId().getLocalValue(), event.getConnectionId().getServerValue());
        }

        @Override
        public void connectionCheckedIn(ConnectionCheckedInEvent event) {
            logger.debug("connectionCheckedIn: {}/{}", event.getConnectionId().getLocalValue(), event.getConnectionId().getServerValue());
        }

        @Override
        public void waitQueueEntered(ConnectionPoolWaitQueueEnteredEvent event) {
            logger.debug("waitQueueEntered: {}/{}", event.getServerId().getAddress());
        }

        @Override
        public void waitQueueExited(ConnectionPoolWaitQueueExitedEvent event) {
            logger.debug("waitQueueExited: {}/{}", event.getServerId().getAddress());
        }

        @Override
        public void connectionAdded(ConnectionAddedEvent event) {
            logger.debug("connectionAdded: {}/{}", event.getConnectionId().getLocalValue(), event.getConnectionId().getServerValue());
        }

        @Override
        public void connectionRemoved(ConnectionRemovedEvent event) {
            logger.debug("connectionRemoved: {}/{}", event.getConnectionId().getLocalValue(), event.getConnectionId().getServerValue());
        }
    }
}