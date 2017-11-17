/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.glytching.dragoman.ql.parser;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelectClauseParserTest {

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

        String expected =
                "package io.github.glytching.dragoman.ql.listener.groovy\n"
                        + "import java.util.Map;\n"
                        + "class GroovyMapper implements Mapper {\n"
                        + "    @Override\n"
                        + "    Map<String, Object> map(Object incoming) {\n"
                        + "       \tMap<String, Object> response = new HashMap<>();\n"
                        + "\t\tresponse.put(\"a\", incoming?.a);\n"
                        + "\t\tresponse.put(\"b.c\", incoming?.b?.c);\n"
                        + "\t\treturn response;\n"
                        + "    }\n"
                        + "}\n";
        assertThat(script, is(expected));
    }

    @Test
    public void cannotParseToAnUnsupportedType() {
        IllegalArgumentException actual =
                assertThrows(IllegalArgumentException.class, () -> parser.get(Object.class, "a, b"));
        assertThat(
                actual.getMessage(),
                containsString("Type: 'Object' is not supported, the supported types are: [String, Bson]"));
    }

    private BsonDocument toBsonDocument(Bson bson) {
        return bson.toBsonDocument(
                BsonDocument.class, CodecRegistries.fromProviders(new BsonValueCodecProvider()));
    }
}
