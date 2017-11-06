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
package org.glytching.dragoman.ql.listener;

import com.google.common.collect.Lists;
import org.glytching.dragoman.antlr.SQLParser;
import org.glytching.dragoman.ql.domain.Projection;
import org.glytching.dragoman.ql.listener.logging.LoggingListener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ANTLR hook for {@code where} expressions. Transforms a {@code select} expression into a collection of
 * {@link Projection}. This should not be used directly, instead use a
 * {@link org.glytching.dragoman.ql.parser.SelectClauseParser}. Note: this is stateful so we one of these per {@code where}
 * expression but as long as the pattern of using a {@link org.glytching.dragoman.ql.parser.SelectClauseParser} is adhered
 * to this statefulness issue is handled.
 *
 * @param <T> the target type, typically either Bson for a MongoDB source or String for a HTTP source
 */
public abstract class AbstractSelectClauseListener<T> extends LoggingListener {

    // intermediate state
    private Optional<Projection> currentProjection;

    // final state
    private final List<Projection> projections;

    public AbstractSelectClauseListener() {
        this.projections = Lists.newArrayList();
        this.currentProjection = Optional.empty();
    }

    @Override
    public void enterIdentifier(SQLParser.IdentifierContext ctx) {
        super.enterIdentifier(ctx);
        if (currentProjection.isPresent()) {
            currentProjection.get().appendNamePart(ctx.start.getText());
        } else {
            currentProjection = Optional.of(new Projection(ctx.start.getText()));
        }
    }

    @Override
    public void enterColumn_reference(SQLParser.Column_referenceContext ctx) {
        super.enterColumn_reference(ctx);
        currentProjection = Optional.empty();
    }

    @Override
    public void exitColumn_reference(SQLParser.Column_referenceContext ctx) {
        super.exitColumn_reference(ctx);
        projections.add(currentProjection.get());
    }

    protected List<Projection> getProjections() {
        return Collections.unmodifiableList(projections);
    }

    public abstract T get();
}