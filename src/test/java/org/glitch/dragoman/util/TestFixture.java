package org.glitch.dragoman.util;

import org.bson.Document;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.reader.DataEnvelope;
import org.glitch.dragoman.store.mongo.MongoStorageCoordinates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestFixture {

    public static Dataset anyDataset() {
        return new Dataset(RandomValues.aString(), RandomValues.aString(), RandomValues.aString(),
                "aSubscriptionControlField", "");
    }

    public static Dataset anyDataset(String user) {
        return new Dataset(user, RandomValues.aString(), RandomValues.aString());
    }

    public static Dataset anyDataset(MongoStorageCoordinates storageCoordinates) {
        return new Dataset(RandomValues.aString(), RandomValues.aString(), storageCoordinates.getDatabaseName() + ":" +
                storageCoordinates.getCollectionName(), "aSubscriptionControlField", "");
    }

    public static Dataset aPersistedDataset() {
        Dataset dataset = anyDataset();
        dataset.setId(UUID.randomUUID().toString());
        return dataset;
    }

    public static DataEnvelope anyDataEnvelope() {
        return new DataEnvelope("aSource", anyMap());
    }

    public static Document anyDocument() {
        return new Document(anyMap());
    }

    public static Map<String, Object> anyMap() {
        return new HashMap<String, Object>() {
            {
                put("aKey", RandomValues.aString());
            }
        };
    }
}