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
package io.github.glytching.dragoman.store.mongo.repository;

import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.repository.Repository;
import io.github.glytching.dragoman.store.mongo.DocumentTransformer;
import org.bson.Document;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.Map;

/**
 * The underlying {@link MongoRepository} returns an observable so it belongs in the repository
 * hierarchy but it deals with {@link Document}. To insulate the reader layer from knowledge of this
 * storage specific, the reader layer uses this repository which transforms {@link Document} to
 * {@code Map<String, Object>}.
 */
public class DecoratingMongoRepository implements Repository<Map<String, Object>> {

    private final MongoRepository mongoRepository;
    private final DocumentTransformer documentTransformer;

    @Inject
    public DecoratingMongoRepository(
            MongoRepository mongoRepository, DocumentTransformer documentTransformer) {
        this.mongoRepository = mongoRepository;
        this.documentTransformer = documentTransformer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Map<String, Object>> find(
            Dataset dataset, String select, String where, String orderBy, int maxResults) {
        return mongoRepository
                .find(dataset, select, where, orderBy, maxResults)
                .map(
                        (Func1<Document, Map<String, Object>>)
                                doc -> documentTransformer.transform(Map.class, doc));
    }

    @Override
    public boolean appliesTo(Dataset dataset) {
        return mongoRepository.appliesTo(dataset);
    }
}
