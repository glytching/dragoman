package org.glitch.dragoman.store.mongo.canned;

import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.dataset.canned.CannedDataset;
import org.glitch.dragoman.dataset.canned.CannedDatasetsLoader;
import org.glitch.dragoman.dataset.canned.CannedDatasetsWriter;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.MongoStorageCoordinates;
import org.glitch.dragoman.store.mongo.dataset.MongoDatasetDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MongoCannedDatasetsWriter implements CannedDatasetsWriter {
    private static final Logger logger = LoggerFactory.getLogger(MongoCannedDatasetsWriter.class);

    private final ApplicationConfiguration configuration;
    private final CannedDatasetsLoader loader;
    private final MongoDatasetDao datasetDao;
    private final MongoProvider mongoProvider;

    @Inject
    public MongoCannedDatasetsWriter(ApplicationConfiguration configuration, CannedDatasetsLoader loader,
                                     MongoDatasetDao datasetDao, MongoProvider mongoProvider) {
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

            cannedDatasets.parallelStream().forEach(cannedDataset -> {
                Dataset dataset = cannedDataset.getDataset();

                logger.info("Writing canned dataset: {}", dataset.getName());

                datasetDao.write(dataset);

                MongoStorageCoordinates storageCoordinates = new MongoStorageCoordinates(dataset.getSource());

                MongoCollection<Document> collection =
                        getCollection(storageCoordinates.getDatabaseName(), storageCoordinates.getCollectionName());

                if (cannedDataset.getDocuments() != null) {
                    List<Success> single =
                            collection.insertMany(toDocuments(cannedDataset.getDocuments())).toList().toBlocking().single();

                    logger.info("Wrote {} documents for canned dataset: {}", single.size(), dataset.getName());
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
        return mongoProvider.provide()
                .getDatabase(databaseName)
                .getCollection(collectionName);
    }
}
