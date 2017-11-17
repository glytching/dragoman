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
package io.github.glytching.dragoman.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

public class JsonTransformerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonTransformer documentTransformer;

    @BeforeEach
    public void setUp() {
        documentTransformer = new JsonTransformer(objectMapper);
    }

    @Test
    public void canTransformMapToJson() {
        Map<String, Object> from = new HashMap<>();
        from.put("a", "b");
        from.put("c", 1);

        String transformed = documentTransformer.transform(from);

        assertThat(transformed, is("{\"a\":\"b\",\"c\":1}"));
    }

    @Test
    public void canTransformJsonToMap() {
        String json = "{\"a\": \"b\", \"c\": 1}";

        //noinspection unchecked
        Map<String, Object> transformed = documentTransformer.transform(Map.class, json);

        assertThat(transformed.size(), is(2));

        assertThat(transformed, hasKey("a"));
        assertThat(transformed.get("a"), is("b"));
        assertThat(transformed, hasKey("c"));
        assertThat(transformed.get("c"), is(1));
    }

    @Test
    public void canPerformTwoWayTransform() {
        Map<String, Object> from = new HashMap<>();
        from.put("a", "b");
        from.put("c", 1);

        String json = documentTransformer.transform(from);

        //noinspection unchecked
        Map<String, Object> actual = documentTransformer.transform(Map.class, json);

        assertThat(actual, is(from));
    }
}
