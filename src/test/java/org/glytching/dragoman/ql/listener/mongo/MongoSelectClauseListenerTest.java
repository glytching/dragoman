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
package org.glytching.dragoman.ql.listener.mongo;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.glytching.dragoman.ql.SqlParserException;
import org.glytching.dragoman.ql.parser.SelectClauseParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MongoSelectClauseListenerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final SelectClauseParser sqlParser = new SelectClauseParser();

    @Test
    public void testSingleProjection() {
        BsonDocument bsonDocument = parse("a,b");

        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("_id", new BsonInt32(0)));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("b", new BsonInt32(1)));
    }

    @Test
    public void testMultipleProjections() {
        BsonDocument bsonDocument = parse("a.b, c.d.e, f, g");

        assertThat(bsonDocument.size(), is(5));
        assertThat(bsonDocument, hasEntry("_id", new BsonInt32(0)));
        assertThat(bsonDocument, hasEntry("a.b", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("c.d.e", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("f", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("g", new BsonInt32(1)));
    }

    @Test
    public void testStarProjection() {
        BsonDocument bsonDocument = parse("*");

        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("_id", new BsonInt32(0)));
    }

    @Test
    public void testEmptyProjection() {
        BsonDocument bsonDocument = parse("");

        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("_id", new BsonInt32(0)));
    }

    @Test
    public void testNullProjection() {
        BsonDocument bsonDocument = parse(null);

        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("_id", new BsonInt32(0)));
    }

    @Test
    public void testInvalidProjection() {
        expectedException.expect(SqlParserException.class);
        expectedException.expectMessage("Line: 1, Position: 3: no viable alternative at input '<EOF>'");

        sqlParser.get(Bson.class, "a, ");
    }

    private BsonDocument parse(String select) {
        Bson bson = sqlParser.get(Bson.class, select);

        return bson.toBsonDocument(BsonDocument.class, CodecRegistries.fromProviders(new BsonValueCodecProvider()));
    }
}