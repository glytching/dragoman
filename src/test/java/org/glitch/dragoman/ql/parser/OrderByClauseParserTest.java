package org.glitch.dragoman.ql.parser;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OrderByClauseParserTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final OrderByClauseParser parser = new OrderByClauseParser();

    @Test
    public void testParseToBson() {
        Bson bson = parser.get(Bson.class, "a, b");

        BsonDocument bsonDocument = toBsonDocument(bson);
        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(-1)));
        assertThat(bsonDocument, hasEntry("b", new BsonInt32(-1)));
    }

    @Test
    public void cannotParseToAnUnsupportedType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Type: 'String' is not supported, the supported types are: [Bson]");

        parser.get(String.class, "a, b");
    }

    private BsonDocument toBsonDocument(Bson bson) {
        return bson.toBsonDocument(BsonDocument.class, CodecRegistries.fromProviders(new BsonValueCodecProvider()));
    }
}