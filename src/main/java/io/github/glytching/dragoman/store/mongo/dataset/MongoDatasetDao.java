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
package io.github.glytching.dragoman.store.mongo.dataset;

import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.rx.client.FindObservable;
import com.mongodb.rx.client.MongoCollection;
import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.dataset.DatasetDao;
import io.github.glytching.dragoman.store.mongo.DocumentTransformer;
import io.github.glytching.dragoman.store.mongo.MongoProvider;
import io.github.glytching.dragoman.store.mongo.MongoStorageCoordinates;
import org.bson.Document;
import org.bson.types.ObjectId;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * An implementation of {@link DatasetDao} for a MongoDB dataset store.
 */
public class MongoDatasetDao implements DatasetDao {

    private final MongoProvider mongoProvider;
    private final DocumentTransformer documentTransformer;
    private final MongoStorageCoordinates storageCoordinates;

    @Inject
    public MongoDatasetDao(
            MongoProvider mongoProvider,
            DocumentTransformer documentTransformer,
            ApplicationConfiguration configuration) {
        this.mongoProvider = mongoProvider;
        this.documentTransformer = documentTransformer;
        this.storageCoordinates =
                new MongoStorageCoordinates(
                        configuration.getDatabaseName(), configuration.getDatasetStorageName());
    }

    @Override
    public Observable<Dataset> getAll(String userName) {
        FindObservable<Document> findObservable = getCollection().find(Filters.eq("owner", userName));

        return findObservable.toObservable().map(toDataset());
    }

    @Override
    public Dataset get(String id) {
        FindObservable<Document> findObservable = getCollection().find(Filters.eq("id", id)).limit(1);

        return findObservable.first().map(toDataset()).toBlocking().singleOrDefault(null);
    }

    @Override
    public boolean exists(String id) {
        Observable<Long> count =
                getCollection().count(Filters.eq("id", id), new CountOptions().limit(1));

        return count.toBlocking().single() > 0;
    }

    @Override
    public long delete(String id) {
        Observable<Document> deleted = getCollection().findOneAndDelete(Filters.eq("id", id));

        Document document = deleted.toBlocking().singleOrDefault(null);

        return document != null ? 1 : 0;
    }

    @Override
    public Dataset write(Dataset dataset) {
        // we populate this on first write and retain it thereafter
        if (isBlank(dataset.getId())) {
            dataset.setId(ObjectId.get().toString());
        }

        Observable<Document> observable =
                getCollection()
                        .findOneAndReplace(
                                Filters.eq("id", dataset.getId()),
                                documentTransformer.transform(dataset),
                                new FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER));

        return documentTransformer.transform(Dataset.class, observable.toBlocking().single());
    }

    private Func1<Document, Dataset> toDataset() {
        return document -> documentTransformer.transform(Dataset.class, document);
    }

    private MongoCollection<Document> getCollection() {
        return mongoProvider
                .provide()
                .getDatabase(storageCoordinates.getDatabaseName())
                .getCollection(storageCoordinates.getCollectionName());
    }
}
