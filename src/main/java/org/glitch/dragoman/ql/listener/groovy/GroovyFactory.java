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
package org.glitch.dragoman.ql.listener.groovy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovySystem;
import org.glitch.dragoman.ql.parser.SelectClauseParser;
import org.glitch.dragoman.ql.parser.WhereClauseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for creating a Groovy classes for a given expression. Why Groovy? so that they can be created and applied
 * dynamically.
 */
public class GroovyFactory {
    private static final Logger logger = LoggerFactory.getLogger(GroovyFactory.class);

    private final GroovyClassLoader groovyClassLoader;
    private final SelectClauseParser selectClauseParser;
    private final WhereClauseParser whereClauseParser;
    private final Cache<String, Object> cache;

    @Inject
    public GroovyFactory(GroovyClassLoader groovyClassLoader, SelectClauseParser selectClauseParser,
                         WhereClauseParser
                                 whereClauseParser) {
        this.groovyClassLoader = groovyClassLoader;
        this.selectClauseParser = selectClauseParser;
        this.whereClauseParser = whereClauseParser;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(2000)
                .initialCapacity(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    /**
     * Delegates to the {@link WhereClauseParser} to parse the given expression, supplying a listener which accepts
     * callbacks from the parser and uses these to create a 'groovified' representation of the given expression.
     *
     * @param expression a 'where clause'
     *
     * @return a Groovy implementation of our Filter, specific to the given expression
     */
    public Filter createFilter(String expression) throws GroovyFactoryException {
        final String script = whereClauseParser.get(String.class, expression);

        logger.debug("From the the expression: {} comes the groovy function: [{}]", expression,
                script);

        // now compile the groovy class and get an instance
        return create(Filter.class, script);
    }

    /**
     * Delegates to the {@link SelectClauseParser} to parse the given expression, supplying a listener which accepts
     * callbacks from the parser and uses these to create a 'groovified' representation of the given expression.
     *
     * @param expression a 'map clause'
     *
     * @return a Groovy implementation of our Mapper, specific to the given expression
     */
    public Mapper createProjector(String expression) throws GroovyFactoryException {
        String script = selectClauseParser.get(String.class, expression);

        logger.debug("From the the expression: {} comes the groovy function: [{}]", expression, script);

        // now compile the groovy class and get an instance
        return create(Mapper.class, script);
    }

    @SuppressWarnings("unchecked")
    private <T> T create(final Class<T> type, final String script) throws GroovyFactoryException {
        try {
            return (T) cache.get(script, () -> {
                // now to compile the groovy class and get an instance
                String name = UUID.randomUUID().toString();
                GroovyCodeSource groovyCodeSource = new GroovyCodeSource(script, name, "/groovy/shell");
                groovyCodeSource.setCachable(true);
                Class<?> clazz = groovyClassLoader.parseClass(groovyCodeSource);
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.warn(e.getMessage(), e);
                    throw new GroovyFactoryException(String.format("Exception creating the %s class instance!",
                            type.getSimpleName()), e);
                }
                for (Class<?> c : groovyClassLoader.getLoadedClasses()) {
                    GroovySystem.getMetaClassRegistry().removeMetaClass(c);
                }
                groovyClassLoader.clearCache();
                return obj;
            });
        } catch (ExecutionException e) {
            logger.warn(e.getMessage(), e);
            throw new GroovyFactoryException(String.format("Exception raised fetching the %s from the cache!",
                    type.getSimpleName()), e);
        }
    }
}