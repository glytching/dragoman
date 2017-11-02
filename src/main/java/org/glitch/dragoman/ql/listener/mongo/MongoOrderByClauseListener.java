package org.glitch.dragoman.ql.listener.mongo;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.conversions.Bson;
import org.glitch.dragoman.ql.domain.OrderBy;
import org.glitch.dragoman.ql.listener.AbstractOrderByClauseListener;

import java.util.List;

public class MongoOrderByClauseListener extends AbstractOrderByClauseListener<Bson> {

    @Override
    public Bson get() {
        BsonDocument orderByObject = new BsonDocument();
        List<OrderBy> orderBys = getOrderBys();
        for (OrderBy orderBy : orderBys) {
            orderByObject.put(orderBy.getName(), new BsonInt32(orderBy.isAscending() ? 1 : -1));
        }
        return orderByObject;
    }
}