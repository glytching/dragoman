package org.glitch.dragoman.ql.parser;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WhereClauseParserTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final WhereClauseParser parser = new WhereClauseParser();

    @Test
    public void testParseToBson() {
        Bson bson = parser.get(Bson.class, "a = 1");

        BsonDocument bsonDocument = toBsonDocument(bson);
        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(1)));
    }

    @Test
    public void testParseToString() {
        String script = parser.get(String.class, "a = 1");

        String expected = "package org.glitch.dragoman.ql.listener.groovy\n" +
                "class GroovyFilter implements Filter {\n" +
                "    @Override\n" +
                "    boolean filter(Object incoming) {\n" +
                "        boolean isSame = incoming?.a==1\n" +
                "        return isSame\n" +
                "    }\n" +
                "}\n";
        assertThat(script, is(expected));
    }

    @Test
    public void cannotParseToAnUnsupportedType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Type: 'Object' is not supported, the supported types are: [String, Bson]");

        parser.get(Object.class, "a, b");
    }

    private BsonDocument toBsonDocument(Bson bson) {
        return bson.toBsonDocument(BsonDocument.class, CodecRegistries.fromProviders(
                new BsonValueCodecProvider(), new ValueCodecProvider()));
    }
}