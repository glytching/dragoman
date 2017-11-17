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
package io.github.glytching.dragoman;

import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import io.github.glytching.dragoman.configuration.constretto.ConstrettoApplicationConfiguration;
import org.constretto.ConstrettoBuilder;
import org.constretto.model.ClassPathResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationConfigurationTest {

    static {
        System.setProperty("env", "test");
    }

    private ApplicationConfiguration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new ConstrettoApplicationConfiguration(new ConstrettoBuilder()
                .addCurrentTag(System.getProperty("env", ""))
                .createPropertiesStore()
                .addResource(new ClassPathResource("test-application.properties"))
                .done()
                .getConfiguration());
    }

    @Test
    public void canReadAnEnvSpecificProperty() {
        // the test case is running with the 'test' TAG
        assertThat(configuration.getHttpPort(), is(9101112));
    }

    @Test
    public void canDeriveAValueForTheRandomPortSymbolic() {
        // we cannot know exactly what value will be produced but it suffices to know that it is > 0 since
        // this means that a value in a valid port range has been derived
        assertThat(configuration.getMongoPort(), greaterThan(0));
    }

    @Test
    public void canReadAStringProperty() {
        assertThat(configuration.getMongoHost(), is("someHost"));
    }

    @Test
    public void canReadAnIntPropertyValue() {
        assertThat(configuration.getConnectionPoolMinSize(), is(5));
    }

    @Test
    public void canReadABooleanPropertyValue() {
        assertThat(configuration.isMetricsEnabled(), is(true));
    }

    @Test
    public void cannotReadANonExistentProperty() {
        RuntimeException actual =
                assertThrows(RuntimeException.class, () -> configuration.getPropertyValue(String.class, "foo"));
        assertThat(actual.getMessage(), startsWith("Failed to read configuration property: Expression [foo] not found"));
    }
}