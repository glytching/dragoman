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
package io.github.glytching.dragoman.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.web.exception.InvalidRequestException;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(RandomBeansExtension.class)
public class ViewTransformerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Random
    private Dataset dataset;
    private ViewTransformer documentTransformer;

    @BeforeEach
    public void setUp() {
        documentTransformer = new ViewTransformer(objectMapper);
    }

    @Test
    public void canPerformTwoWayTransform() {
        String json = documentTransformer.transform(dataset);

        Dataset actual = documentTransformer.transform(Dataset.class, json);

        assertThat(actual, is(dataset));
    }

    @Test
    public void willThrowAnInvalidRequestExceptionIfTheGivenJsonCannotBeDeserialised() {
        String json = "{\"a\": \"b\", \"c\": 1}";

        InvalidRequestException actual =
                assertThrows(
                        InvalidRequestException.class,
                        () -> documentTransformer.transform(Dataset.class, json));
        assertThat(actual.getMessage(), startsWith("Failed to deserialise request body"));
    }
}
