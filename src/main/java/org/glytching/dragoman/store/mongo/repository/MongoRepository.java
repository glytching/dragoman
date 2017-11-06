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
package org.glytching.dragoman.store.mongo.repository;

import com.mongodb.rx.client.FindObservable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.ql.parser.OrderByClauseParser;
import org.glytching.dragoman.ql.parser.SelectClauseParser;
import org.glytching.dragoman.ql.parser.WhereClauseParser;
import org.glytching.dragoman.repository.Repository;
import org.glytching.dragoman.store.mongo.MongoProvider;
import org.glytching.dragoman.store.mongo.MongoStorageCoordinates;
import org.glytching.dragoman.util.StopWatch;
import org.glytching.dragoman.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;

/**
 * An implementation of {@link Repository} for MongoDB data sources.
 */
public class MongoRepository implements Repository<Document> {
    private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);

    private final SelectClauseParser selectClauseParser;
    private final WhereClauseParser whereClauseParser;
    private final OrderByClauseParser orderByClauseParser;
    private final MongoProvider mongoProvider;
    private final UrlUtils urlUtils;

    @Inject
    public MongoRepository(SelectClauseParser selectClauseParser, WhereClauseParser whereClauseParser,
                           OrderByClauseParser orderByClauseParser, MongoProvider mongoProvider, UrlUtils urlUtils) {
        this.selectClauseParser = selectClauseParser;
        this.whereClauseParser = whereClauseParser;
        this.orderByClauseParser = orderByClauseParser;
        this.mongoProvider = mongoProvider;
        this.urlUtils = urlUtils;
    }

    @Override
    public Observable<Document> find(Dataset dataset, String select, String where, String orderBy, int maxResults) {
        MongoStorageCoordinates storageCoordinates = new MongoStorageCoordinates(dataset.getSource());
        StopWatch stopWatch = StopWatch.startForSplits();
        Bson projections = selectClauseParser.get(Bson.class, select);
        long projectionElapsedTime = stopWatch.split();

        Bson filter = whereClauseParser.get(Bson.class, where);
        long predicateElapsedTime = stopWatch.split();

        Bson order = orderByClauseParser.get(Bson.class, orderBy);
        long orderByElapsedTime = stopWatch.split();

        FindObservable<Document> findObservable = mongoProvider.provide()
                .getDatabase(storageCoordinates.getDatabaseName())
                .getCollection(storageCoordinates.getCollectionName())
                .find(filter)
                .projection(projections)
                .sort(order);

        if (maxResults > 0) {
            findObservable.limit(maxResults);
        }
        long findElapsedTime = stopWatch.split();

        long totalElapsedTime = stopWatch.stop();

        logger.info("Total elapsed time for find call={}ms (projection={}ms, predicate={}ms, orderBy={}ms, find={}ms)",
                totalElapsedTime, projectionElapsedTime, predicateElapsedTime, orderByElapsedTime, findElapsedTime);

        return findObservable.toObservable();
    }

    @Override
    public boolean appliesTo(Dataset dataset) {
        return !urlUtils.isUrl(dataset.getSource());
    }
}