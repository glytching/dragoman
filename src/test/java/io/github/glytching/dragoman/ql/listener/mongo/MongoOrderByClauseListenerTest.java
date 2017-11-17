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
package io.github.glytching.dragoman.ql.listener.mongo;

import io.github.glytching.dragoman.ql.SqlParserException;
import io.github.glytching.dragoman.ql.parser.OrderByClauseParser;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MongoOrderByClauseListenerTest {

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
    SqlParserException actual =
        assertThrows(SqlParserException.class, () -> sqlParser.get(Bson.class, "a, "));
    assertThat(
        actual.getMessage(),
        containsString("Line: 1, Position: 3: no viable alternative at input '<EOF>'"));
  }

  private BsonDocument parse(String orderBy) {
    Bson bson = sqlParser.get(Bson.class, orderBy);

    return bson.toBsonDocument(
        BsonDocument.class, CodecRegistries.fromProviders(new BsonValueCodecProvider()));
  }
}
