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
package org.glitch.dragoman.store.http.repository;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListAwareResponsePostProcessorTest {

    @Test
    public void willWrapAResponseInAnArray() {
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor();

        String actual = responsePostProcessor.postProcess("{\"a\": \"b\"}, {\"c\": 1}");

        assertThat(actual, is("[{\"a\": \"b\"}, {\"c\": 1}]"));
    }

    @Test
    public void willDoNothingIfTheResponseIsAlreadyAnArray() {
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor();

        String actual = responsePostProcessor.postProcess("[{\"a\": \"b\"}, {\"c\": 1}]");

        assertThat(actual, is("[{\"a\": \"b\"}, {\"c\": 1}]"));
    }

    @Test
    public void willDoNothingIfTheResponseIsEmpty() {
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor();

        String actual = responsePostProcessor.postProcess("");

        assertThat(actual, is(""));
    }

    @Test
    public void willDelegateToTheWrappedProcessor() {
        ResponsePostProcessor delegate = mock(ResponsePostProcessor.class);
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor(delegate);

        String content = "{\"a\": \"b\"}, {\"c\": 1}";
        when(delegate.postProcess(content)).thenReturn(content);

        String actual = responsePostProcessor.postProcess(content);

        assertThat(actual, is("[{\"a\": \"b\"}, {\"c\": 1}]"));
    }
}
