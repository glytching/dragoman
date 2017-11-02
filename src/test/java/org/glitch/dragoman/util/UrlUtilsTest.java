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
package org.glitch.dragoman.util;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UrlUtilsTest {

    private final UrlUtils urlUtils = new UrlUtils();

    @Test
    public void canTellWhetherAStringIsAUrl() {
        assertThat(urlUtils.isUrl("a:b"), is(false));

        assertThat(urlUtils.isUrl("http://aHost:12345/"), is(true));

        assertThat(urlUtils.isUrl("https://a:1234"), is(true));

        assertThat(urlUtils.isUrl("http://a:1234/some/end/point?a=b"), is(true));
    }
}