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
package io.github.glytching.dragoman.ql.listener.groovy;

import com.google.common.collect.Maps;
import groovy.lang.GroovyClassLoader;
import io.github.glytching.dragoman.ql.parser.SelectClauseParser;
import io.github.glytching.dragoman.ql.parser.WhereClauseParser;
import io.github.glytching.dragoman.util.MapMaker;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GroovyWhereClauseListenerTest {
  private static final Logger logger = LoggerFactory.getLogger(GroovyWhereClauseListenerTest.class);

  private final GroovyFactory filterFactory =
      new GroovyFactory(new GroovyClassLoader(), new SelectClauseParser(), new WhereClauseParser());

  @Test
  public void testEquals() throws Exception {
    String where = "a = 1 and b = 'hello' and c = 2.2 and d = '2017-09-12'";

    Map<String, Object> document =
        MapMaker.makeMap(
            MapMaker.makeEntry("a", 1),
            MapMaker.makeEntry("b", "hello"),
            MapMaker.makeEntry("c", 2.2),
            MapMaker.makeEntry("d", "2017-09-12"));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("b", "goodbye");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testNotEquals() throws Exception {
    String where = "a != 1 and b != 'hello' and c != 2.2 and d != '2017-09-12'";

    Map<String, Object> document =
        MapMaker.makeMap(
            MapMaker.makeEntry("a", 2),
            MapMaker.makeEntry("b", "goodbye"),
            MapMaker.makeEntry("c", 3.3),
            MapMaker.makeEntry("d", "2017-09-11"));

    assertThat(filter(where, document), is(true));
  }

  @Test
  public void testGreaterThan() throws Exception {
    String where = "a > 1 and b >= 2.2";

    Map<String, Object> document =
        MapMaker.makeMap(MapMaker.makeEntry("a", 2), MapMaker.makeEntry("b", 2.2));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("a", 0);
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testLessThan() throws Exception {
    String where = "a < 1 and b <= 2.2";

    Map<String, Object> document =
        MapMaker.makeMap(MapMaker.makeEntry("a", 0), MapMaker.makeEntry("b", 2.2));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("a", 2);
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testBetween() throws Exception {
    String where = "a between 1 and 5 and b not between 2.5 and 4.5";

    Map<String, Object> document =
        MapMaker.makeMap(MapMaker.makeEntry("a", 1), MapMaker.makeEntry("b", 1.5));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("b", 2.8);
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testIn() throws Exception {
    String where = "a in (1, 2) and b in ('x', 'y')";

    Map<String, Object> document =
        MapMaker.makeMap(MapMaker.makeEntry("a", 1), MapMaker.makeEntry("b", "y"));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("b", "z");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testNotIn() throws Exception {
    String where = "a not in (1, 2) and b not in ('x', 'y')";

    Map<String, Object> document =
        MapMaker.makeMap(MapMaker.makeEntry("a", 3), MapMaker.makeEntry("b", "z"));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("b", "y");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testLike() throws Exception {
    String where = "a like 'foo%' and b like '%usic' and c like '%ancin%'";

    Map<String, Object> document =
        MapMaker.makeMap(
            MapMaker.makeEntry("a", "food"),
            MapMaker.makeEntry("b", "music"),
            MapMaker.makeEntry("c", "dancing"));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("a", "drinking");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testNotLike() throws Exception {
    String where = "a not like 'foo%'";

    Map<String, Object> document = MapMaker.makeMap(MapMaker.makeEntry("a", "dancing"));

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("a", "food");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testNull() throws Exception {
    String where = "a is null";

    Map<String, Object> document = Maps.newHashMap();
    document.put("a", null);

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("a", "this is not null!");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testNotNull() throws Exception {
    String where = "a is not null";

    Map<String, Object> document = Maps.newHashMap();
    document.put("a", "this is not null!");

    assertThat(filter(where, document), is(true));

    // test the flip side to avoid a false positive
    document.put("a", null);
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void canHandleNegativeNumbers() throws Exception {
    String where = "a > -1";

    Map<String, Object> document = MapMaker.makeMap(MapMaker.makeEntry("a", 2));

    assertThat(filter(where, document), is(true));
  }

  @Test
  public void testCombination() throws Exception {
    String where =
        "a = 1 "
            + "and b != 2.2 "
            + "and c = 'a string value' "
            + "and d != 'another string value' "
            + "and e > 1 "
            + "and f < 2 "
            + "and g >= 3 "
            + "and h <= 4 "
            + "and i between 5 and 7 "
            + "and j in (8, 9) "
            + "and k in ('x', 'y') "
            + "and l not in (10, 11) "
            + "and m like '%foo%' "
            + "and n not like 'bar%' "
            + "and o is not null "
            + "and p is null "
            + "and q > -5";

    Map<String, Object> document =
        MapMaker.makeMap(
            MapMaker.makeEntry("a", 1),
            MapMaker.makeEntry("b", 3.3),
            MapMaker.makeEntry("c", "a string value"),
            MapMaker.makeEntry("d", "something"),
            MapMaker.makeEntry("e", 5),
            MapMaker.makeEntry("f", 1),
            MapMaker.makeEntry("g", 3),
            MapMaker.makeEntry("h", 4),
            MapMaker.makeEntry("i", 6.1),
            MapMaker.makeEntry("j", 8),
            MapMaker.makeEntry("k", "x"),
            MapMaker.makeEntry("l", 12),
            MapMaker.makeEntry("m", "food"),
            MapMaker.makeEntry("o", "a non null value"),
            MapMaker.makeEntry("q", -2));
    document.put("p", null);

    assertThat(filter(where, document), is(true));

    // test the flip-side to avoid a false positive
    document.remove("a");
    assertThat(filter(where, document), is(false));
  }

  @Test
  public void testNestedReferences() throws Exception {
    String where = "a.b > 1 and c.d.e = 'hello'";

    Map<String, Object> document =
        MapMaker.makeMap(
            MapMaker.makeEntry("a", MapMaker.makeMap(MapMaker.makeEntry("b", 2))),
            MapMaker.makeEntry(
                "c",
                MapMaker.makeMap(
                    MapMaker.makeEntry("d", MapMaker.makeMap(MapMaker.makeEntry("e", "hello"))))));

    assertThat(filter(where, document), is(true));
  }

  @Test
  public void testEmptyPredicate() throws Exception {
    String where = "";

    Map<String, Object> document = MapMaker.makeMap(MapMaker.makeEntry("a", 1));

    assertThat(filter(where, document), is(true));
  }

  @Test
  public void testNullPredicate() throws Exception {
    String where = "";

    Map<String, Object> document = MapMaker.makeMap(MapMaker.makeEntry("a", 1));

    assertThat(filter(where, document), is(true));
  }

  private boolean filter(String where, Map<String, Object> document) throws Exception {
    Filter filter = filterFactory.createFilter(where);

    boolean result = filter.filter(document);

    logger.debug("{} document: {}", (result ? "Filtered" : "Unfiltered"), document);

    return result;
  }
}
