package org.glitch.dragoman.store.mongo.repository;

import org.bson.Document;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.repository.Repository;
import org.glitch.dragoman.store.mongo.DocumentTransformer;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.Map;

/**
 * The underlying {@link MongoRepository} returns an observable so it belongs in the repository hierarchy but it deals
 * with {@link Document}. To insulate the reader layer from knowledge of this storage specific, the reader layer uses
 * this repository which transforms {@link Document} to {@code Map<String, Object>}.
 */
public class DecoratingMongoRepository implements Repository<Map<String, Object>> {

    private final MongoRepository mongoRepository;
    private final DocumentTransformer documentTransformer;

    @Inject
    public DecoratingMongoRepository(MongoRepository mongoRepository, DocumentTransformer documentTransformer) {
        this.mongoRepository = mongoRepository;
        this.documentTransformer = documentTransformer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Map<String, Object>> find(Dataset dataset, String select, String where, String orderBy,
                                                int maxResults) {
        return mongoRepository.find(dataset, select, where, orderBy, maxResults)
                .map((Func1<Document, Map<String, Object>>) doc -> documentTransformer.transform(Map.class, doc));
    }

    @Override
    public boolean appliesTo(Dataset dataset) {
        return mongoRepository.appliesTo(dataset);
    }
}