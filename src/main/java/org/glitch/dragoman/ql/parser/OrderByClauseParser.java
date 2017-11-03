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
import org.glitch.dragoman.ql.listener.AbstractOrderByClauseListener;
import org.glitch.dragoman.ql.listener.mongo.MongoOrderByClauseListener;

import static java.lang.String.format;

/**
 * An implementation of {@link BaseParser} for {@code orderBy} expressions. Example usage:
 * <p>
 * <pre>
 *     OrderByClauseParser parser = new OrderByClauseParser();
 *     Bson bson = parser.get(Bson.class, "a, b");
 * </pre>
 */
public class OrderByClauseParser extends BaseParser {

    /**
     * Get a deserialised form of the given {@code expression}, deserialised into the type {@code T}. See
     * {@link #getListener(Class)} to understand what target types are supported.
     *
     * @param clazz the target type e.g. Bson if you want to apply the {@code expression} to a MongoDB store
     * @param expression the order by expression
     * @param <T>
     *
     * @return a deserialised form of the given {@code expression}, deserialised into the type {@code T}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String expression) {
        AbstractOrderByClauseListener<T> listener = getListener(clazz);

        parse(expression, listener);

        return listener.get();
    }

    @Override
    protected ParserRuleContext getParserContext(SQLParser parser) {
        // this is the entry point for an order by clause
        return parser.sort_specifier_list();
    }

    private <T> AbstractOrderByClauseListener getListener(Class<T> clazz) {
        if (Bson.class == clazz) {
            return new MongoOrderByClauseListener();
        } else {
            throw new IllegalArgumentException(format("Type: '%s' is not supported, the supported types are: [%s]",
                    clazz.getSimpleName(), Bson.class.getSimpleName()));
        }
    }
}