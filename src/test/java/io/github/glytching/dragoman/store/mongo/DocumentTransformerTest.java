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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.glytching.dragoman.dataset.Dataset;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.glytching.dragoman.util.TestFixture.aPersistedDataset;
import static io.github.glytching.dragoman.util.TestFixture.anyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DocumentTransformerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private DocumentTransformer documentTransformer;

  @BeforeEach
  public void setUp() {
    documentTransformer = new DocumentTransformer(objectMapper);
  }

  @Test
  public void canTransformMapToDocument() {
    Map<String, Object> from = anyMap();

    Document transformed = documentTransformer.transform(from);

    assertThat(transformed.size(), is(from.size()));

    for (Map.Entry<String, Object> entry : from.entrySet()) {
      assertThat(transformed, hasKey(entry.getKey()));
      assertThat(transformed.get(entry.getKey()), is(entry.getValue()));
    }
  }

  @Test
  public void canTransformJsonToDocument() {
    String json = "{\"a\": \"b\", \"c\": 1}";

    Document transformed = documentTransformer.transform(json);

    assertThat(transformed.size(), is(2));
    assertThat(transformed, hasKey("a"));
    assertThat(transformed.get("a"), is("b"));
    assertThat(transformed, hasKey("c"));
    assertThat(transformed.get("c"), is(1));
  }

  @Test
  public void canTransformDocumentToMap() {
    Document document = new Document().append("a", "b").append("c", 1);

    //noinspection unchecked
    Map<String, Object> transformed = documentTransformer.transform(Map.class, document);

    assertThat(transformed.size(), is(document.size()));

    for (Map.Entry<String, Object> entry : document.entrySet()) {
      assertThat(transformed, hasKey(entry.getKey()));
      assertThat(transformed.get(entry.getKey()), is(entry.getValue()));
    }
  }

  @Test
  public void willDiscardTheMongoIdentifier() {
    Document document = new Document("_id", "abc").append("a", "b").append("c", 1);

    //noinspection unchecked
    Map<String, Object> actual = documentTransformer.transform(Map.class, document);

    assertThat(actual, not(hasKey("_id")));
  }

  @Test
  public void canPerformTwoWayTransform() {
    Dataset dataset = aPersistedDataset();

    Document document = documentTransformer.transform(dataset);

    Dataset actual = documentTransformer.transform(Dataset.class, document);

    assertThat(actual, is(dataset));
  }
}
