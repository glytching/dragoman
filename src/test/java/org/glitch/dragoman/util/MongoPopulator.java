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
package org.glitch.dragoman.util;

import com.jayway.awaitility.Awaitility;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import org.bson.Document;
import org.glitch.removeables.simulator.DocumentSimulatedEventFactory;
import org.glitch.removeables.simulator.DocumentSimulator;
import org.glitch.removeables.simulator.NoOpSimulatorDelay;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.concurrent.atomic.AtomicBoolean;

public class MongoPopulator {
    private static final Logger logger = LoggerFactory.getLogger(MongoPopulator.class);

    //  quick tool which generates data from the simulator and writes it into a named db.coll in MongoDB
    private final String databaseName = "dragoman";
    private final String sample = "sample5";

    @Test
    public void populate() {
        DocumentSimulator documentSimulator = new DocumentSimulator(new NoOpSimulatorDelay(),
                new DocumentSimulatedEventFactory());

        Observable<Document> simulate = documentSimulator.simulate(1);

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(sample);

        final AtomicBoolean finished = new AtomicBoolean(false);
        simulate.subscribe(document -> {
                    logger.debug("About to insert ...");
                    collection.insertOne(document).toList().toBlocking().single();
                    logger.debug("Inserted ...");
                },
                throwable -> logger.error("Failed to insert document!", throwable),
                () -> {
                    logger.info("Finished writing!");
                    finished.set(true);
                });
        Awaitility.await().until(finished::get);
    }
}