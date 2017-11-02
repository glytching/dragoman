package org.glitch.dragoman.store.mongo.repository;

import com.mongodb.rx.client.FindObservable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.ql.parser.OrderByClauseParser;
import org.glitch.dragoman.ql.parser.SelectClauseParser;
import org.glitch.dragoman.ql.parser.WhereClauseParser;
import org.glitch.dragoman.repository.Repository;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.MongoStorageCoordinates;
import org.glitch.dragoman.util.StopWatch;
import org.glitch.dragoman.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;

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