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
package org.glytching.dragoman.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.web.exception.InvalidRequestException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class ViewTransformerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ViewTransformer documentTransformer;

    @Before
    public void setUp() {
        documentTransformer = new ViewTransformer(objectMapper);
    }

    @Test
    public void canPerformTwoWayTransform() {
        Dataset dataset = anyDataset();

        String json = documentTransformer.transform(dataset);

        Dataset actual = documentTransformer.transform(Dataset.class, json);

        assertThat(actual, is(dataset));
    }

    @Test
    public void willThrowAnInvalidRequestExceptionIfTheGivenJsonCannotBeDeserialised() {
        expectedException.expect(InvalidRequestException.class);
        expectedException.expectMessage(startsWith("Failed to deserialise request body"));

        String json = "{\"a\": \"b\", \"c\": 1}";

        documentTransformer.transform(Dataset.class, json);
    }
}