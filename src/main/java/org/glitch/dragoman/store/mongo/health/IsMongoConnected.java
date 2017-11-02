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
package org.glitch.dragoman.store.mongo.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.ServerAddress;
import com.mongodb.rx.client.MongoClient;
import org.glitch.dragoman.store.mongo.MongoProvider;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class IsMongoConnected extends HealthCheck {

    private final MongoProvider mongoProvider;

    @Inject
    public IsMongoConnected(MongoProvider mongoProvider) {
        this.mongoProvider = mongoProvider;
    }

    @Override
    protected Result check() throws Exception {
        MongoClient mongoClient = mongoProvider.provide();

        List<ServerAddress> serverAddresses = mongoClient.getSettings().getClusterSettings().getHosts();
        String address = serverAddresses
                .stream()
                .map(ServerAddress::toString)
                .collect(Collectors.joining(","));

        try {
            // any read will suffice to prove connectivity
            mongoClient.getDatabase("xyz");
            return Result.healthy("Connected to MongoDB at " + address);
        } catch (Exception ex) {
            return Result.unhealthy("Cannot connect to MongoDB at " + address);
        }
    }
}
