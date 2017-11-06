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
package org.glytching.dragoman.ql.parser;

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