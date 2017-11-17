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
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WhereClauseParserTest {

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

    String expected =
        "package io.github.glytching.dragoman.ql.listener.groovy\n"
            + "class GroovyFilter implements Filter {\n"
            + "    @Override\n"
            + "    boolean filter(Object incoming) {\n"
            + "        boolean isSame = incoming?.a==1\n"
            + "        return isSame\n"
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
        BsonDocument.class,
        CodecRegistries.fromProviders(new BsonValueCodecProvider(), new ValueCodecProvider()));
  }
}
