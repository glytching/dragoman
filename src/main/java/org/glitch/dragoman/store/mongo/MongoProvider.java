package org.glitch.dragoman.store.mongo;

import com.mongodb.rx.client.MongoClient;

public interface MongoProvider {

    MongoClient provide();
}