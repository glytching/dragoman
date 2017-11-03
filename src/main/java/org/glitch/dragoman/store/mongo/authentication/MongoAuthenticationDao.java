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
package org.glitch.dragoman.store.mongo.authentication;

import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.FindObservable;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.glitch.dragoman.authentication.AuthenticationDao;
import org.glitch.dragoman.authentication.PasswordUtil;
import org.glitch.dragoman.authentication.User;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.store.mongo.DocumentTransformer;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.MongoStorageCoordinates;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;

import static java.lang.String.format;

/**
 * An implementation of {@link AuthenticationDao} for a MongoDB authentication store.
 */
public class MongoAuthenticationDao implements AuthenticationDao {

    private final MongoProvider mongoProvider;
    private final DocumentTransformer documentTransformer;
    private final PasswordUtil passwordUtil;
    private final MongoStorageCoordinates storageCoordinates;

    @Inject
    public MongoAuthenticationDao(MongoProvider mongoProvider, DocumentTransformer documentTransformer,
                                  PasswordUtil passwordUtil, ApplicationConfiguration configuration) {
        this.mongoProvider = mongoProvider;
        this.documentTransformer = documentTransformer;
        this.passwordUtil = passwordUtil;
        this.storageCoordinates = new MongoStorageCoordinates(configuration.getDatabaseName(),
                configuration.getUserStorageName());
    }

    @Override
    public boolean exists(String userName) {
        Observable<Long> count = getCollection().count(Filters.eq("name", userName), new CountOptions().limit(1));
        return count.toBlocking().single() > 0;
    }

    @Override
    public boolean isValid(String userName, String password) {
        Observable<Long> count =
                getCollection().count(filter(userName, passwordUtil.toHash(password)), new CountOptions().limit(1));
        return count.toBlocking().single() > 0;
    }

    @Override
    public User getUser(String userName, String password) {
        FindObservable<Document> findObservable = getCollection()
                .find(filter(userName, passwordUtil.toHash(password)))
                .limit(1);

        return findObservable.first().map(toUser()).toBlocking().singleOrDefault(null);
    }

    @Override
    public void createUser(String userName, String password) {
        if (exists(userName)) {
            throw new RuntimeException(format("A user already exists for: %s!", userName));
        } else {
            Observable<Success> successObservable =
                    getCollection().insertOne(documentTransformer.transform(new User(userName, passwordUtil.toHash
                            (password))));

            Success success = successObservable.toBlocking().single();
            if (success != Success.SUCCESS) {
                throw new RuntimeException(format("Failed to create user for: %s!", userName));
            }
        }
    }

    private Bson filter(String name, String hashedPassword) {
        return Filters.and(Filters.eq("name", name), Filters.eq("hashedPassword", hashedPassword));
    }

    private Func1<Document, User> toUser() {
        return document -> documentTransformer.transform(User.class, document);
    }

    private MongoCollection<Document> getCollection() {
        return mongoProvider.provide()
                .getDatabase(storageCoordinates.getDatabaseName())
                .getCollection(storageCoordinates.getCollectionName());
    }
}