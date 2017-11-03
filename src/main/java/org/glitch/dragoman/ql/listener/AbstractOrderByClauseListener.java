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
package org.glitch.dragoman.ql.listener;

import com.google.common.collect.Lists;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.domain.OrderBy;
import org.glitch.dragoman.ql.listener.logging.LoggingListener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ANTLR hook for {@code where} expressions. Transforms a {@code orderBy} expression into a collection of
 * {@link OrderBy}. This should not be used directly, instead use a
 * {@link org.glitch.dragoman.ql.parser.OrderByClauseParser}. Note: this is stateful so we one of these per {@code where}
 * expression but as long as the pattern of using a {@link org.glitch.dragoman.ql.parser.OrderByClauseParser} is adhered
 * to this statefulness issue is handled.
 *
 * @param <T> the target type, typically either Bson for a MongoDB source or String for a HTTP source
 */
public abstract class AbstractOrderByClauseListener<T> extends LoggingListener {

    // intermediate state
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<String> currentOrderByField;
    private boolean currentOrderBySpecifier;

    // final results
    private final List<OrderBy> orderBys;

    public AbstractOrderByClauseListener() {
        this.orderBys = Lists.newArrayList();
        this.currentOrderByField = Optional.empty();
    }

    @Override
    public void enterIdentifier(SQLParser.IdentifierContext ctx) {
        super.enterIdentifier(ctx);
        currentOrderByField = currentOrderByField.map(s -> Optional.of(s + '.' + ctx.start.getText()))
                .orElseGet(() -> Optional.of(ctx.start.getText()));
    }

    @Override
    public void enterSort_specifier(SQLParser.Sort_specifierContext ctx) {
        super.enterSort_specifier(ctx);
        currentOrderByField = Optional.empty();
        currentOrderBySpecifier = false;
    }

    @Override
    public void exitSort_specifier(SQLParser.Sort_specifierContext ctx) {
        super.exitSort_specifier(ctx);
        orderBys.add(new OrderBy(currentOrderByField.get(), currentOrderBySpecifier));
    }

    @Override
    public void enterOrder_specification(SQLParser.Order_specificationContext ctx) {
        super.enterOrder_specification(ctx);
        currentOrderBySpecifier = ctx.getText().equalsIgnoreCase("asc");
    }

    protected List<OrderBy> getOrderBys() {
        return Collections.unmodifiableList(orderBys);
    }

    public abstract T get();
}