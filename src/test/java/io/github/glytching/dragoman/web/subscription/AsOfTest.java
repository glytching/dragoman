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
package io.github.glytching.dragoman.web.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AsOfTest {

    private AsOfFormatter asOfFormatter;
    private LocalDateTime lastRead;

    @BeforeEach
    public void setUp() {
        asOfFormatter = new AsOfFormatter();

        lastRead = LocalDateTime.now(ZoneId.of("UTC"));
    }

    @Test
    public void willFormatTheAsOfClauseUsingTheGivenDateTimeLiteral() {
        String asOfFieldPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        AsOf asOf = new AsOf("updatedAt", asOfFieldPattern, lastRead, asOfFormatter);

        String actual = asOf.applyAsOf("x > 1");

        assertThat(
                actual,
                is(
                        "x > 1 and updatedAt > '"
                                + DateTimeFormatter.ofPattern(asOfFieldPattern).format(lastRead)
                                + "'"));
    }

    @Test
    public void willFormatTheAsOfClauseUsingTheEpochMillisIfANumericPatternIsSupplied() {
        AsOf asOf = new AsOf("updatedAt", "L", lastRead, asOfFormatter);

        String actual = asOf.applyAsOf("x > 1");

        assertThat(
                actual, is("x > 1 and updatedAt > " + lastRead.toInstant(ZoneOffset.UTC).toEpochMilli()));
    }

    @Test
    public void willFormatTheAsOfClauseUsingTheDefaultDateTimeLiteralIfNoPatternIsSupplied() {
        AsOf asOf = new AsOf("updatedAt", "", lastRead, asOfFormatter);

        String actual = asOf.applyAsOf("x > 1");

        assertThat(
                actual,
                is(
                        "x > 1 and updatedAt > '"
                                + DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(lastRead)
                                + "'"));
    }

    @Test
    public void willFailIfGivenAnUnsupportedPattern() {
        RuntimeException actual =
                assertThrows(
                        RuntimeException.class, () -> new AsOf("updatedAt", "foo", lastRead, asOfFormatter));
        assertThat(actual.getMessage(), is("Cannot format AsOf for an unsupported pattern: foo!"));
    }
}
