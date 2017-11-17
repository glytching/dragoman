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
package io.github.glytching.dragoman.store.mongo.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.rx.client.MongoClient;
import io.github.glytching.dragoman.store.mongo.MongoProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class IsMongoConnectedTest {

    @Mock
    private MongoProvider mongoProvider;
    @Mock
    private MongoClient mongoClient;

    private String host;
    private int port;

    private IsMongoConnected isMongoConnected;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        isMongoConnected = new IsMongoConnected(mongoProvider);

        host = "ahost";
        port = 1234;
        when(mongoProvider.provide()).thenReturn(mongoClient);
        when(mongoClient.getSettings()).thenReturn(mongoSettings(host, port));
    }

    @Test
    public void isHealthyIfWeCanTalkToMongo() throws Exception {
        HealthCheck.Result result = isMongoConnected.check();

        assertThat(result.isHealthy(), is(true));
        assertThat(result.getMessage(), is("Connected to MongoDB at " + host + ":" + port));

        verify(mongoClient, times(1)).getDatabase(anyString());
    }

    @Test
    public void isUnhealthyIfWeCannotTalkToMongo() throws Exception {
        when(mongoClient.getDatabase(anyString())).thenThrow(new RuntimeException());

        HealthCheck.Result result = isMongoConnected.check();

        assertThat(result.isHealthy(), is(false));
        assertThat(result.getMessage(), is("Cannot connect to MongoDB at " + host + ":" + port));
    }

    private MongoClientSettings mongoSettings(String host, int port) {
        return MongoClientSettings.builder()
                .clusterSettings(
                        ClusterSettings.builder().hosts(newArrayList(new ServerAddress(host, port))).build())
                .build();
    }
}
