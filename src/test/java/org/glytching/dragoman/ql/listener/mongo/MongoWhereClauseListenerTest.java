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

import org.bson.*;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;
import org.glytching.dragoman.ql.SqlParserException;
import org.glytching.dragoman.ql.parser.WhereClauseParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MongoWhereClauseListenerTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final WhereClauseParser sqlParser = new WhereClauseParser();

    @Test
    public void testEquals() {
        BsonDocument bsonDocument = parse("a = 'foo' and b = 1");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonString("foo")));
        assertThat(bsonDocument, hasEntry("b", new BsonInt32(1)));
    }

    @Test
    public void testNotEquals() {
        BsonDocument bsonDocument = parse("a != 'b' and c != 1");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$ne", new BsonString("b"))));
        assertThat(bsonDocument, hasEntry("c", new BsonDocument("$ne", new BsonInt32(1))));
    }

    @Test
    public void testGreaterThan() {
        BsonDocument bsonDocument = parse("a > 1 and b >= 2");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$gt", new BsonInt32(1))));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$gte", new BsonInt32(2))));
    }

    @Test
    public void testLessThan() {
        BsonDocument bsonDocument = parse("a < 1 and b <= 2");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$lt", new BsonInt32(1))));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$lte", new BsonInt32(2))));
    }

    @Test
    public void testBetween() {
        BsonDocument bsonDocument = parse("a between 5 and 10");

        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument()
                .append("$gte", new BsonInt32(5))
                .append("$lt", new BsonInt32(10))));
    }

    @Test
    public void testIn() {
        BsonDocument bsonDocument = parse("a in ('this', 'that') and b in (5, 10)");

        assertThat(bsonDocument.size(), is(2));

        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$in", new BsonArray(
                newArrayList(new BsonString("this"), new BsonString("that"))))));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$in", new BsonArray(
                newArrayList(new BsonInt32(5), new BsonInt32(10))))));
    }

    @Test
    public void testNotIn() {
        BsonDocument bsonDocument = parse("a not in ('this', 'that') and b not in (5, 10)");

        assertThat(bsonDocument.size(), is(2));

        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$nin", new BsonArray(
                newArrayList(new BsonString("this"), new BsonString("that"))))));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$nin", new BsonArray(
                newArrayList(new BsonInt32(5), new BsonInt32(10))))));
    }

    @Test
    public void testLike() {
        BsonDocument bsonDocument = parse("a like '%x%' and b like '%y' and c like 'z%'");

        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("a", new BsonRegularExpression("x", "")));
        assertThat(bsonDocument, hasEntry("b", new BsonRegularExpression("y$", "")));
        assertThat(bsonDocument, hasEntry("c", new BsonRegularExpression("^z", "")));
    }

    @Test
    public void testNotLike() {
        BsonDocument bsonDocument = parse("a not like '%x%' and b not like '%y' and c not like 'z%'");

        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$not", new BsonRegularExpression("x", ""))));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$not", new BsonRegularExpression("y$", ""))));
        assertThat(bsonDocument, hasEntry("c", new BsonDocument("$not", new BsonRegularExpression("^z", ""))));
    }

    @Test
    public void testLikeWithReservedCharacters() {
        BsonDocument bsonDocument = parse("a like '%x*%' and b like '%y+' and c like '$z%'");

        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("a", new BsonRegularExpression("x\\*", "")));
        assertThat(bsonDocument, hasEntry("b", new BsonRegularExpression("y\\+$", "")));
        assertThat(bsonDocument, hasEntry("c", new BsonRegularExpression("^\\$z", "")));
    }

    @Test
    public void testNull() {
        BsonDocument bsonDocument = parse("a is null and b is null");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonNull()));
        assertThat(bsonDocument, hasEntry("b", new BsonNull()));
    }

    @Test
    public void testNotNull() {
        BsonDocument bsonDocument = parse("a is not null and b is not null");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$ne", new BsonNull())));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$ne", new BsonNull())));
    }

    @Test
    public void testNegativeNumbers() {
        String where = "a > -1";

        BsonDocument bsonDocument = parse(where);

        assertThat(bsonDocument.size(), is(1));
        assertThat(bsonDocument, hasEntry("a", new BsonDocument("$gt", new BsonInt32(-1))));
    }

    @Test
    public void testCombination() {
        String where = "a = 1 " +
                "and b != 2.2 " +
                "and c = 'a string value' " +
                "and d != 'another string value' " +
                "and e > 1 " +
                "and f < 2 " +
                "and g >= 3 " +
                "and h <= 4 " +
                "and i between 5 and 7 " +
                "and j in (8, 9) " +
                "and k in ('x', 'y') " +
                "and l not in (10, 11) " +
                "and m like '%foo%' " +
                "and n not like 'bar%' " +
                "and o is not null " +
                "and p is null " +
                "and q > -5";

        BsonDocument bsonDocument = parse(where);

        assertThat(bsonDocument.size(), is(17));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(1)));
        assertThat(bsonDocument, hasEntry("b", new BsonDocument("$ne", new BsonDouble(2.2))));
        assertThat(bsonDocument, hasEntry("c", new BsonString("a string value")));
        assertThat(bsonDocument, hasEntry("d", new BsonDocument("$ne", new BsonString("another string value"))));
        assertThat(bsonDocument, hasEntry("e", new BsonDocument("$gt", new BsonInt32(1))));
        assertThat(bsonDocument, hasEntry("f", new BsonDocument("$lt", new BsonInt32(2))));
        assertThat(bsonDocument, hasEntry("g", new BsonDocument("$gte", new BsonInt32(3))));
        assertThat(bsonDocument, hasEntry("h", new BsonDocument("$lte", new BsonInt32(4))));
        assertThat(bsonDocument, hasEntry("i", new BsonDocument().append("$gte", new BsonInt32(5)).append("$lt", new BsonInt32(7))));
        assertThat(bsonDocument, hasEntry("j", new BsonDocument("$in", new BsonArray(newArrayList(new BsonInt32(8), new BsonInt32(9))))));
        assertThat(bsonDocument, hasEntry("k", new BsonDocument("$in", new BsonArray(newArrayList(new BsonString("x"), new BsonString("y"))))));
        assertThat(bsonDocument, hasEntry("l", new BsonDocument("$nin", new BsonArray(newArrayList(new BsonInt32(10), new BsonInt32(11))))));
        assertThat(bsonDocument, hasEntry("m", new BsonRegularExpression("foo", "")));
        assertThat(bsonDocument, hasEntry("n", new BsonDocument("$not", new BsonRegularExpression("^bar", ""))));
        assertThat(bsonDocument, hasEntry("o", new BsonDocument("$ne", new BsonNull())));
        assertThat(bsonDocument, hasEntry("p", new BsonNull()));
        assertThat(bsonDocument, hasEntry("q", new BsonDocument("$gt", new BsonInt32(-5))));
    }

    @Test
    public void testNestedReferences() {
        String where = "a.b > 1 and c.d.e = 'hello'";

        BsonDocument bsonDocument = parse(where);

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a.b", new BsonDocument("$gt", new BsonInt32(1))));
        assertThat(bsonDocument, hasEntry("c.d.e", new BsonString("hello")));
    }

    @Test
    public void testEmptyWhereClause() {
        BsonDocument bsonDocument = parse("");

        assertThat(bsonDocument.size(), is(0));
    }

    @Test
    public void testNullWhereClause() {
        BsonDocument bsonDocument = parse("");

        assertThat(bsonDocument.size(), is(0));
    }

    @Test
    public void testBooleanValues() {
        BsonDocument bsonDocument = parse("a = true and b = false");

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonBoolean(true)));
        assertThat(bsonDocument, hasEntry("b", new BsonBoolean(false)));
    }

    @Test
    public void testNumericValues() {
        BsonDocument bsonDocument = parse(
                format("a = %d and b = %d and c = %.1f", Integer.MAX_VALUE, Long.MAX_VALUE, 1.2));

        assertThat(bsonDocument.size(), is(3));
        assertThat(bsonDocument, hasEntry("a", new BsonInt32(Integer.MAX_VALUE)));
        assertThat(bsonDocument, hasEntry("b", new BsonInt64(Long.MAX_VALUE)));
        assertThat(bsonDocument, hasEntry("c", new BsonDouble(1.2)));
    }

    @Test
    public void testDateLiteralValues() {
        String dateLiteral = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String dateTimeLiteral = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        BsonDocument bsonDocument = parse(format("a = '%s' and b = '%s'", dateLiteral, dateTimeLiteral));

        assertThat(bsonDocument.size(), is(2));
        assertThat(bsonDocument, hasEntry("a", new BsonDateTime(
                LocalDate.parse(dateLiteral).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())));
        assertThat(bsonDocument, hasEntry("b", new BsonDateTime(
                LocalDateTime.parse(dateTimeLiteral).toInstant(ZoneOffset.UTC).toEpochMilli())));
    }

    @Test
    public void testUnsupportedDateLiteralValues() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(containsString("Failed to parse date/time literal"));

        parse("a = '2017-10-27 10:05:45'");
    }

    @Test
    public void testInvalidPredicate() {
        expectedException.expect(SqlParserException.class);
        expectedException.expectMessage("Line: 1, Position: 4: no viable alternative at input '<EOF>'");

        sqlParser.get(Bson.class, "a = ");
    }

    @Test
    public void testCombinationOfValidAndInvalidPredicates() {
        expectedException.expect(SqlParserException.class);
        expectedException.expectMessage("Line: 1, Position: 16: no viable alternative at input 'bnottrue', " +
                "Line: 1, Position: 28: no viable alternative at input '<EOF>'");

        sqlParser.get(Bson.class, "a = 1 and b not true and c =");
    }

    private BsonDocument parse(String where) {
        Bson bson = sqlParser.get(Bson.class, where);

        return bson.toBsonDocument(BsonDocument.class, CodecRegistries.fromProviders(
                new BsonValueCodecProvider(), new ValueCodecProvider()));
    }
}