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
package io.github.glytching.dragoman.ql.parser;

import io.github.glytching.dragoman.antlr.SQLParser;
import io.github.glytching.dragoman.ql.listener.AbstractWhereClauseListener;
import io.github.glytching.dragoman.ql.listener.groovy.GroovyWhereClauseListener;
import io.github.glytching.dragoman.ql.listener.mongo.MongoWhereClauseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.bson.conversions.Bson;

import static java.lang.String.format;

/**
 * An implementation of {@link BaseParser} for {@code where} expressions. Example usage:
 * <p>
 * <pre>
 *     WhereClauseParser parser = new WhereClauseParser();
 *
 *     // for use with a MongoDB store
 *     Bson bson = parser.get(Bson.class, "a = 1");
 *
 *     // for use with a HTTP store
 *     String script = parser.get(String.class, "a = 1");
 * </pre>
 */
public class WhereClauseParser extends BaseParser {

    /**
     * Get a deserialised form of the given {@code expression}, deserialised into the type {@code T}. See
     * {@link #getListener(Class)} to understand what target types are supported.
     *
     * @param clazz the target type e.g. Bson if you want to apply the {@code expression} to a MongoDB store
     * @param expression the where expression
     * @param <T>
     *
     * @return a deserialised form of the given {@code expression}, deserialised into the type {@code T}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String expression) {
        AbstractWhereClauseListener<T> listener = getListener(clazz);

        parse(expression, listener);

        return listener.get();
    }

    @Override
    protected ParserRuleContext getParserContext(SQLParser parser) {
        // this is the entry point for a where clause
        return parser.search_condition();
    }

    private <T> AbstractWhereClauseListener getListener(Class<T> clazz) {
        if (String.class == clazz) {
            return new GroovyWhereClauseListener();
        } else if (Bson.class == clazz) {
            return new MongoWhereClauseListener();
        } else {
            throw new IllegalArgumentException(format("Type: '%s' is not supported, the supported types are: [%s, %s]",
                    clazz.getSimpleName(), String.class.getSimpleName(), Bson.class.getSimpleName()));
        }
    }
}
