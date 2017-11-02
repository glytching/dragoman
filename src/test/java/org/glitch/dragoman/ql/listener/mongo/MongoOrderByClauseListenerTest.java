package org.glitch.dragoman.ql.listener.mongo;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.glitch.dragoman.ql.SqlParserException;
import org.glitch.dragoman.ql.parser.OrderByClauseParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MongoOrderByClauseListenerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final OrderByClauseParser sqlParser = new OrderByClauseParser();

    @Test
    public void testSingleField() {
        String orderBy = "a desc";

        BsonDocument bsonDocument = parse(orderBy);

        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(-1)));
    }

    @Test
    public void testMultipleFields() {
        String orderBy = "a asc, b desc, c.d.e asc";

        BsonDocument bsonDocument = parse(orderBy);

        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("b", new BsonInt32(-1)));
        assertThat(bsonDocument, hasEntry("c.d.e", new BsonInt32(1)));
    }

    @Test
    public void testDefaultOrderIsDescending() {
        String orderBy = "a, b.c";

        BsonDocument bsonDocument = parse(orderBy);

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(-1)));
        assertThat(bsonDocument, hasEntry("b.c", new BsonInt32(-1)));
    }

    @Test
    public void testInvalidOrderBy() {
        expectedException.expect(SqlParserException.class);
        expectedException.expectMessage("Line: 1, Position: 3: no viable alternative at input '<EOF>'");

        sqlParser.get(Bson.class, "a, ");
    }

    private BsonDocument parse(String orderBy) {
        Bson bson = sqlParser.get(Bson.class, orderBy);

        return bson.toBsonDocument(BsonDocument.class, CodecRegistries.fromProviders(new BsonValueCodecProvider()));
    }
}