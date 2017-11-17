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
package io.github.glytching.dragoman.store.mongo.canned;

import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.dataset.canned.CannedDataset;
import io.github.glytching.dragoman.dataset.canned.CannedDatasetsLoader;
import io.github.glytching.dragoman.dataset.canned.CannedDatasetsWriter;
import io.github.glytching.dragoman.store.mongo.MongoProvider;
import io.github.glytching.dragoman.store.mongo.MongoStorageCoordinates;
import io.github.glytching.dragoman.store.mongo.dataset.MongoDatasetDao;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * An implementation of {@link CannedDatasetsWriter} for a MongoDB dataset store.
 */
public class MongoCannedDatasetsWriter implements CannedDatasetsWriter {
    private static final Logger logger = LoggerFactory.getLogger(MongoCannedDatasetsWriter.class);

    private final ApplicationConfiguration configuration;
    private final CannedDatasetsLoader loader;
    private final MongoDatasetDao datasetDao;
    private final MongoProvider mongoProvider;

    @Inject
    public MongoCannedDatasetsWriter(
            ApplicationConfiguration configuration,
            CannedDatasetsLoader loader,
            MongoDatasetDao datasetDao,
            MongoProvider mongoProvider) {
        this.configuration = configuration;
        this.loader = loader;
        this.datasetDao = datasetDao;
        this.mongoProvider = mongoProvider;
    }

    @Override
    public int write() {
        AtomicInteger count = new AtomicInteger(0);
        if (isNotBlank(configuration.getCannedDatasetsDirectory())) {
            List<CannedDataset> cannedDatasets = loader.load(configuration.getCannedDatasetsDirectory());

            cannedDatasets
                    .parallelStream()
                    .forEach(
                            cannedDataset -> {
                Dataset dataset = cannedDataset.getDataset();

                logger.info("Writing canned dataset: {}", dataset.getName());

                datasetDao.write(dataset);

                                MongoStorageCoordinates storageCoordinates =
                                        new MongoStorageCoordinates(dataset.getSource());

                MongoCollection<Document> collection =
                        getCollection(
                                storageCoordinates.getDatabaseName(),
                                storageCoordinates.getCollectionName());

                if (cannedDataset.getDocuments() != null) {
                    List<Success> single =
                            collection
                                    .insertMany(toDocuments(cannedDataset.getDocuments()))
                                    .toList()
                                    .toBlocking()
                                    .single();

                    logger.info(
                            "Wrote {} documents for canned dataset: {}",
                            single.size(),
                            dataset.getName());
                }

                logger.info("Wrote canned dataset: {}", dataset.getName());

                count.incrementAndGet();
                            });
        }

        return count.get();
    }

    private List<Document> toDocuments(List<Map<String, Object>> documents) {
        return documents.stream().map(Document::new).collect(Collectors.toList());
    }

    private MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        return mongoProvider.provide().getDatabase(databaseName).getCollection(collectionName);
  }
}
