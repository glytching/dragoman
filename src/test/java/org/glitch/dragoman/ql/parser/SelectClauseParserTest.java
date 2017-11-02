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

public class SelectClauseParserTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final SelectClauseParser parser = new SelectClauseParser();

    @Test
    public void testParseToBson() {
        Bson bson = parser.get(Bson.class, "a, b.c");

        BsonDocument bsonDocument = toBsonDocument(bson);
        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("_id", new BsonInt32(0)));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("b.c", new BsonInt32(1)));
    }

    @Test
    public void testParseToString() {
        String script = parser.get(String.class, "a, b.c");

        String expected = "package org.glitch.dragoman.ql.listener.groovy\n" +
                "import java.util.Map;\n" +
                "class GroovyMapper implements Mapper {\n" +
                "    @Override\n" +
                "    Map<String, Object> map(Object incoming) {\n" +
                "       \tMap<String, Object> response = new HashMap<>();\n" +
                "\t\tresponse.put(\"a\", incoming?.a);\n" +
                "\t\tresponse.put(\"b.c\", incoming?.b?.c);\n" +
                "\t\treturn response;\n" +
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
        return bson.toBsonDocument(BsonDocument.class, CodecRegistries.fromProviders(new BsonValueCodecProvider()));
    }
}