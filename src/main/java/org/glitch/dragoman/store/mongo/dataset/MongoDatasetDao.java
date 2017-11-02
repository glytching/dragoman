package org.glitch.dragoman.store.mongo.dataset;

import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.rx.client.FindObservable;
import com.mongodb.rx.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.dataset.DatasetDao;
import org.glitch.dragoman.store.mongo.DocumentTransformer;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.MongoStorageCoordinates;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class MongoDatasetDao implements DatasetDao {

    private final MongoProvider mongoProvider;
    private final DocumentTransformer documentTransformer;
    private final MongoStorageCoordinates storageCoordinates;

    @Inject
    public MongoDatasetDao(MongoProvider mongoProvider, DocumentTransformer documentTransformer,
                           ApplicationConfiguration configuration) {
        this.mongoProvider = mongoProvider;
        this.documentTransformer = documentTransformer;
        this.storageCoordinates = new MongoStorageCoordinates(configuration.getDatabaseName(),
                configuration.getDatasetStorageName());
    }

    @Override
    public Observable<Dataset> getAll(String user) {
        FindObservable<Document> findObservable = getCollection()
                .find(Filters.eq("owner", user));

        return findObservable.toObservable().map(toDataset());
    }

    @Override
    public Dataset get(String id) {
        FindObservable<Document> findObservable = getCollection()
                .find(Filters.eq("id", id))
                .limit(1);

        return findObservable.first().map(toDataset()).toBlocking().single();
    }

    @Override
    public boolean exists(String id) {
        Observable<Long> count = getCollection().count(Filters.eq("id", id), new CountOptions().limit(1));

        return count.toBlocking().single() > 0;
    }

    @Override
    public long delete(String id) {
        Observable<Document> deleted = getCollection().findOneAndDelete(Filters.eq("id", id));

        Document document = deleted.toBlocking().single();

        return document != null ? 1 : 0;
    }

    @Override
    public Dataset write(Dataset dataset) {
        // we populate this on first write and retain it thereafter
        if (isBlank(dataset.getId())) {
            dataset.setId(ObjectId.get().toString());
        }

        Observable<Document> observable =
                getCollection().findOneAndReplace(Filters.eq("id", dataset.getId()),
                        documentTransformer.transform(dataset),
                        new FindOneAndReplaceOptions
                                ().upsert(true).returnDocument(ReturnDocument.AFTER));

        return documentTransformer.transform(Dataset.class, observable.toBlocking().single());
    }

    private Func1<Document, Dataset> toDataset() {
        return document -> documentTransformer.transform(Dataset.class, document);
    }

    private MongoCollection<Document> getCollection() {
        return mongoProvider.provide()
                .getDatabase(storageCoordinates.getDatabaseName())
                .getCollection(storageCoordinates.getCollectionName());
    }
}