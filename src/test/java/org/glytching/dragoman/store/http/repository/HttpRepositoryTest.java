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
package org.glytching.dragoman.store.http.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.bson.Document;
import org.glytching.dragoman.configuration.guice.HttpModule;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.store.http.AbstractHttpTestCase;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRepositoryTest extends AbstractHttpTestCase {

    @Inject
    private HttpRepository repository;

    private Dataset dataset;

    private Map<String, Object> bill;
    private Map<String, Object> martin;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(Modules.override(new HttpModule()).with(new HttpOverrideModule()));
        injector.injectMembers(this);

        bill = Maps.newHashMap();
        bill.put("name", "Bill");
        bill.put("type", "Human");
        bill.put("age", 35);
        bill.put("biped", true);
        bill.put("shoeSize", 9);
        bill.put("rating", 1.2);
        bill.put("comments", "likes pianos");
        bill.put("expirationDate", "2027-10-27");
        bill.put("createdAt", LocalDateTime.now().toString());

        martin = Maps.newHashMap();
        martin.put("name", "Martin");
        martin.put("type", "Martian");
        martin.put("age", 1156);
        martin.put("biped", false);
        martin.put("shoeSize", 4.5);
        martin.put("rating", null);
        martin.put("comments", "interplanetary travel");
        martin.put("expirationDate", "2117-10-27");
        martin.put("createdAt", LocalDateTime.now().toString());

        when(httpDataProvider.getAll()).thenReturn(Lists.newArrayList(bill, martin));

        dataset = mock(Dataset.class);
        // point us at the in-process HTTP server
        when(dataset.getSource()).thenReturn(getUrl());
    }

    @Test
    public void appliesToHttpSources() {
        Dataset dataset = anyDataset();

        dataset.setSource("http://foo:1234");
        assertThat(repository.appliesTo(dataset), is(true));

        dataset.setSource("a:b");
        assertThat(repository.appliesTo(dataset), is(false));
    }

    @Test
    public void withEqualsAndNotEquals() {
        String expression = "name = 'Bill' and name != 'Bob'";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(bill));
    }

    @Test
    public void withSelect() {
        String expression = "name = 'Bill' and name != 'Bob'";

        List<Map<String, Object>> results = toList(repository.find(dataset, "name, age", expression, "", -1));

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(new Document().append("name", bill.get("name")).append("age", bill.get("age"))));
    }

    @Test
    public void withMaxResults() {
        String expression = "name is not null";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", 1));

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(bill));
    }

    @Test
    public void withGreaterThanAndLessThan() {
        String expression = "age > 1000 and shoeSize < 5";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(martin));
    }

    @Test
    public void withGreaterThanOrEqualToAndLessThanOrEqualTo() {
        String expression = "age <= 35 and shoeSize <= 9";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results.get(0), is(bill));
    }

    @Test
    public void withInStrings() {
        String expression = "name in ('Bill', 'Martin')";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(2));
        assertThat(results, hasItem(bill));
        assertThat(results, hasItem(martin));
    }

    @Test
    public void withInNumerics() {
        String expression = "age in (35, 1156)";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(2));
        assertThat(results, hasItem(bill));
        assertThat(results, hasItem(martin));
    }

    @Test
    public void withNotIn() {
        String expression = "age not in (35, 53)";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(martin));
    }

    @Test
    public void withBetween() {
        String expression = "age between 30 and 40";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(bill));
    }

    @Test
    public void withNull() {
        String expression = "rating is null";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(martin));
    }

    @Test
    public void withNotNull() {
        String expression = "rating is not null";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(bill));
    }

    @Test
    public void withLikeAndNotLike() {
        String expression = "type like '%uman%' and name like 'Bil%' and name not like '%ob'";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(bill));
    }

    @Test
    public void withBoolean() {
        String expression = "biped = true";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(bill));
    }

    @Test
    public void withDateLiteral() {
        String expression = "expirationDate = '2027-10-27'";

        List<Map<String, Object>> results = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(results.size(), is(1));
        assertThat(results, hasItem(bill));
    }

    private List<Map<String, Object>> toList(Observable<Map<String, Object>> observable) {
        return observable.toList().toBlocking().single();
    }
}