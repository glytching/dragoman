package org.glitch.dragoman.ql.listener.mongo;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.conversions.Bson;
import org.glitch.dragoman.ql.domain.Projection;
import org.glitch.dragoman.ql.listener.AbstractSelectClauseListener;

import java.util.List;

public class MongoSelectClauseListener extends AbstractSelectClauseListener<Bson> {

    @Override
    public Bson get() {
        List<Projection> projections = getProjections();
        BsonDocument response = new BsonDocument("_id", new BsonInt32(0));
        for (Projection p : projections) {
            response.append(p.getName(), new BsonInt32(1));
        }

        return response;
    }
}