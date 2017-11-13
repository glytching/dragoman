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
package org.glytching.dragoman.util;

import org.bson.Document;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.reader.DataEnvelope;
import org.glytching.dragoman.store.mongo.MongoStorageCoordinates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class TestFixture {

    public static Dataset anyDataset() {
        return random(Dataset.class);
    }

    public static Dataset anyDataset(String user) {
        Dataset dataset = random(Dataset.class, "user");
        dataset.setOwner(user);
        return dataset;
    }

    public static Dataset anyDataset(MongoStorageCoordinates storageCoordinates) {
        Dataset dataset = random(Dataset.class, "source");
        dataset.setSource(storageCoordinates.getDatabaseName() + ":" + storageCoordinates.getCollectionName());
        return dataset;
    }

    public static Dataset aPersistedDataset() {
        Dataset dataset = new Dataset(aString(), aString(), aString(), "aSubscriptionControlField", "");
        dataset.setId(UUID.randomUUID().toString());
        return dataset;
    }

    public static DataEnvelope anyDataEnvelope() {
        return new DataEnvelope(aString(), anyMap());
    }

    public static Document anyDocument() {
        return new Document(anyMap());
    }

    public static Map<String, Object> anyMap() {
        return new HashMap<String, Object>() {
            {
                put("aKey", aString());
            }
        };
    }

    public static String aString() {
        return random(String.class);
    }
}