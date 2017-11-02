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
package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.constretto.ConstrettoBuilder;
import org.constretto.model.ClassPathResource;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.configuration.constretto.ConstrettoApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ConfigurationModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationModule.class);

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public ApplicationConfiguration provideApplicationConfiguration() {
        String env = System.getProperty("env", "");

        if (isNotBlank(env)) {
            logger.info("Configuring system for env={}", env);
        }

        return new ConstrettoApplicationConfiguration(new ConstrettoBuilder()
                .addCurrentTag(env)
                .createPropertiesStore()
                .addResource(new ClassPathResource("application.properties"))
                .done()
                .getConfiguration());
    }
}