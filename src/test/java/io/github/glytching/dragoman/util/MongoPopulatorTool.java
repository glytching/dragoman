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

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * A simple tool for inserting into a MongoDB collection, useful for manual testing of the
 * subscription feature for MongoDB datasets.
 */
public class MongoPopulatorTool {
    private static final Logger logger = LoggerFactory.getLogger(MongoPopulatorTool.class);

    //  quick tool which writes data into a named db.coll in MongoDB
    private final String databaseName = "dragoman";
    private final String sample = "sample5";

    @Test
    public void populate() {
        int simulationCount = 1;

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(sample);

        for (int i = 0; i < simulationCount; i++) {
            Document d = new Document("name", "Person " + i).append("updatedAt", new Date());
            collection.insertOne(d).toList().toBlocking().single();
        }
    }
}
