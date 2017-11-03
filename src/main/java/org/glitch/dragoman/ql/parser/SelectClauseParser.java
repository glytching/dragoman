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
package org.glitch.dragoman.ql.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.bson.conversions.Bson;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.listener.AbstractSelectClauseListener;
import org.glitch.dragoman.ql.listener.groovy.GroovySelectClauseListener;
import org.glitch.dragoman.ql.listener.mongo.MongoSelectClauseListener;

import static java.lang.String.format;

/**
 * An implementation of {@link BaseParser} for {@code select} expressions. Example usage:
 * <p>
 * <pre>
 *     SelectClauseParser parser = new SelectClauseParser();
 *
 *     // for use with a MongoDB store
 *     Bson bson = parser.get(Bson.class, "a, b.c");
 *
 *     // for use with a HTTP store
 *     String script = parser.get(String.class, "a, b.c");
 * </pre>
 */
public class SelectClauseParser extends BaseParser {

    /**
     * Get a deserialised form of the given {@code expression}, deserialised into the type {@code T}. See
     * {@link #getListener(Class)} to understand what target types are supported.
     *
     * @param clazz the target type e.g. Bson if you want to apply the {@code expression} to a MongoDB store
     * @param expression the select expression
     * @param <T>
     *
     * @return a deserialised form of the given {@code expression}, deserialised into the type {@code T}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String expression) {
        AbstractSelectClauseListener<T> listener = getListener(clazz);

        parse(expression, listener);

        return listener.get();
    }

    @Override
    protected ParserRuleContext getParserContext(SQLParser parser) {
        // this is the entry point for a map clause
        return parser.select_list();
    }

    private <T> AbstractSelectClauseListener getListener(Class<T> clazz) {
        if (String.class == clazz) {
            return new GroovySelectClauseListener();
        } else if (Bson.class == clazz) {
            return new MongoSelectClauseListener();
        } else {
            throw new IllegalArgumentException(format("Type: '%s' is not supported, the supported types are: [%s, %s]",
                    clazz.getSimpleName(), String.class.getSimpleName(), Bson.class.getSimpleName()));
        }
    }
}