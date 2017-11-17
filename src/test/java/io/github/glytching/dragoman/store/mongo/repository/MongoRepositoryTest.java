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
package io.github.glytching.dragoman.store.mongo.repository;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import io.github.glytching.dragoman.configuration.guice.ConfigurationModule;
import io.github.glytching.dragoman.configuration.guice.MongoModule;
import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.store.mongo.AbstractMongoDBTest;
import io.github.glytching.dragoman.store.mongo.MongoProvider;
import io.github.glytching.dragoman.store.mongo.MongoStorageCoordinates;
import io.github.glytching.dragoman.util.StopWatch;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.github.glytching.dragoman.util.TestFixture.anyDataset;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MongoRepositoryTest extends AbstractMongoDBTest {
    private static final Logger logger = LoggerFactory.getLogger(MongoRepositoryTest.class);

    @Inject
    private MongoRepository repository;
    @Inject
    private MongoProvider mongoProvider;

    private Document bill;
    private Document martin;
    private MongoStorageCoordinates storageCoordinates;
    private Dataset dataset;

    @BeforeEach
    public void setUp() {
        Injector injector =
                Guice.createInjector(
                        Modules.override(new MongoModule(), new ConfigurationModule())
                                .with(new MongoOverrideModule()));
        injector.injectMembers(this);

        bill =
                new Document("name", "Bill")
                        .append("type", "Human")
                        .append("age", 35)
                        .append("biped", true)
                        .append("shoeSize", 9)
                        .append("rating", 1.2)
                        .append("comments", "likes pianos")
                        .append("expirationDate", toDate(LocalDate.parse("2017-10-27").plusYears(10)))
                        .append("createdAt", toDate(LocalDateTime.now()));

        martin =
                new Document("name", "Martin")
                        .append("type", "Martian")
                        .append("age", 1156)
                        .append("biped", false)
                        .append("shoeSize", 4.5)
                        .append("rating", null)
                        .append("comments", "interplanetary travel")
                        .append("expirationDate", toDate(LocalDate.parse("2017-10-27").plusYears(100)))
                        .append("createdAt", toDate(LocalDateTime.now()));

        storageCoordinates = seed(bill, martin);

        dataset = mock(Dataset.class);
        when(dataset.getSource())
                .thenReturn(
                        storageCoordinates.getDatabaseName() + ":" + storageCoordinates.getCollectionName());

        when(mongoProvider.provide()).thenReturn(getMongoClient());
    }

    @AfterEach
    public void tearDown() {
        StopWatch stopWatch = StopWatch.start();
        getMongoClient()
                .getDatabase(storageCoordinates.getDatabaseName())
                .drop()
                .subscribe(
                        success -> logger.info("Dropped database: {}", storageCoordinates.getDatabaseName()));
        logger.info("Dropped test data in {}ms", stopWatch.stop());
    }

    @Test
    public void appliesToAnythingOtherThanAHttpSource() {
        Dataset dataset = anyDataset();

        dataset.setSource("http://foo:1234");
        assertThat(repository.appliesTo(dataset), is(false));

        dataset.setSource("a:b");
        assertThat(repository.appliesTo(dataset), is(true));
    }

    @Test
    public void withEqualsAndNotEquals() {
        String expression = "name = 'Bill' and name != 'Bob'";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));

        assertThat(documents.size(), is(1));
        assertThat(documents.get(0), is(bill));
    }

    @Test
    public void withSelect() {
        String expression = "name = 'Bill' and name != 'Bob'";

        List<Document> documents = toList(repository.find(dataset, "name, age", expression, "", -1));

        assertThat(documents.size(), is(1));
        assertThat(
                documents.get(0),
                is(new Document().append("name", bill.get("name")).append("age", bill.get("age"))));
    }

    @Test
    public void withOrderBy() {
        String expression = "name is not null";

        List<Document> documents =
                toList(repository.find(dataset, "*", expression, "age desc, expirationDate desc", -1));

        assertThat(documents.size(), is(2));
        assertThat(documents.get(0), is(martin));
        assertThat(documents.get(1), is(bill));
    }

    @Test
    public void withMaxResults() {
        String expression = "name is not null";

        List<Document> documents = toList(repository.find(dataset, "", expression, "name asc", 1));

        assertThat(documents.size(), is(1));
        assertThat(documents.get(0), is(bill));
    }

    @Test
    public void withGreaterThanAndLessThan() {
        String expression = "age > 1000 and shoeSize < 5";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents.get(0), is(martin));
    }

    @Test
    public void withGreaterThanOrEqualToAndLessThanOrEqualTo() {
        String expression = "age <= 35 and shoeSize <= 9";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents.get(0), is(bill));
    }

    @Test
    public void withInStrings() {
        String expression = "name in ('Bill', 'Martin')";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(2));
        assertThat(documents, hasItem(bill));
        assertThat(documents, hasItem(martin));
    }

    @Test
    public void withInNumerics() {
        String expression = "age in (35, 1156)";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(2));
        assertThat(documents, hasItem(bill));
        assertThat(documents, hasItem(martin));
    }

    @Test
    public void withNotIn() {
        String expression = "age not in (35, 53)";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(martin));
    }

    @Test
    public void withBetween() {
        String expression = "age between 30 and 40";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(bill));
    }

    @Test
    public void withNull() {
        String expression = "rating is null";

        List<Document> documents =
                repository.find(dataset, "", expression, "", -1).toList().toBlocking().single();
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(martin));
    }

    @Test
    public void withNotNull() {
        String expression = "rating is not null";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(bill));
    }

    @Test
    public void withLikeAndNotLike() {
        String expression = "type like '%uman%' and name like 'Bil%' and name not like '%ob'";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(bill));
    }

    @Test
    public void withBoolean() {
        String expression = "biped = true";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(bill));
    }

    @Test
    public void withDateLiteral() {
        String expression = "expirationDate = '2027-10-27'";

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(1));
        assertThat(documents, hasItem(bill));
    }

    @Test
    public void withDateTimeLiteral() {
        String expression =
                format("createdAt < '%s'", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));

        List<Document> documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(2));
        assertThat(documents, hasItem(bill));
        assertThat(documents, hasItem(martin));

        expression =
                format("createdAt > '%s'", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));

        documents = toList(repository.find(dataset, "", expression, "", -1));
        assertThat(documents.size(), is(0));
    }

    private List<Document> toList(Observable<Document> observable) {
        return observable.toList().toBlocking().single();
    }
}
