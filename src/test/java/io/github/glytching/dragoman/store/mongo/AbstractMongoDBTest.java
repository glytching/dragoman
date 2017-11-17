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
package io.github.glytching.dragoman.store.mongo;

import com.google.common.collect.Lists;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.Network;
import io.github.glytching.dragoman.util.StopWatch;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class AbstractMongoDBTest {
  private static final Logger logger = LoggerFactory.getLogger(AbstractMongoDBTest.class);

  private static final AtomicInteger nextId = new AtomicInteger();

  private static final MongodStarter starter =
      MongodStarter.getInstance(
          new RuntimeConfigBuilder()
              .defaults(Command.MongoD)
              .processOutput(ProcessOutput.getDefaultInstanceSilent())
              .build());

  private static MongodExecutable mongodExe;
  private static MongodProcess mongod;
  private static int port;
  private static MongodForTestsFactory factory;

  private MongoClient mongoClient;

  //
  // we create a single embedded mongo instance for all tests because we do not want to incur the
  // creation cost
  // _per test_
  //

  @BeforeAll
  public static void start() throws Exception {
    StopWatch stopWatch = StopWatch.startForSplits();
    port = Network.getFreeServerPort();
    mongodExe =
        starter.prepare(
            new MongodConfigBuilder()
                .version(Version.Main.DEVELOPMENT)
                .net(new Net("localhost", port, Network.localhostIsIPv6()))
                .build());
    long prepareElapsedTime = stopWatch.split();
    mongod = mongodExe.start();
    long startElapsedTime = stopWatch.split();
    logger.info(
        "Started embedded Mongo in {}ms (prepareElapsedTime={}ms, startElapsedTime={}ms)",
        stopWatch.stop(),
        prepareElapsedTime,
        startElapsedTime);
  }

  @AfterAll
  public static void stop() {
    StopWatch stopWatch = StopWatch.startForSplits();
    try {
      logger.info("Stopping embedded mongod");
      mongod.stop();
      long stopMongodElapsedTime = stopWatch.split();
      mongodExe.stop();
      long stopMongoExeElapsedTime = stopWatch.split();
      logger.info(
          "Stopped embedded Mongo in {}ms (stopMongodElapsedTime={}ms, stopMongoExeElapsedTime={}ms)",
          stopWatch.stop(),
          stopMongodElapsedTime,
          stopMongoExeElapsedTime);
    } catch (Exception ex) {
      logger.warn("Failed to stop embedded mongod!", ex);
    }
  }

  protected MongoClient getMongoClient() {
    if (mongoClient == null) {
      mongoClient = MongoClients.create("mongodb://localhost:" + port);
    }
    return mongoClient;
  }

  protected MongoStorageCoordinates seed(Document... documents) {
    String databaseName = createDatabaseName();
    String collectionName = createCollectionName();
    MongoCollection<Document> mongoCollection =
        getMongoClient().getDatabase(databaseName).getCollection(collectionName);

    mongoCollection
        .insertMany(Lists.newArrayList(documents))
        .timeout(10, SECONDS)
        .toBlocking()
        .single();

    for (Document document : documents) {
      document.remove("_id");
    }

    assertThat(
        "Failed to seed the given documents!",
        mongoCollection.count().toBlocking().single(),
        is((long) documents.length));

    return new MongoStorageCoordinates(databaseName, collectionName);
  }

  protected Date toDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

  protected Date toDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC));
  }

  private String createDatabaseName() {
    return "database" + nextId.getAndIncrement();
  }

  private String createCollectionName() {
    return "collection" + nextId.getAndIncrement();
  }
}
