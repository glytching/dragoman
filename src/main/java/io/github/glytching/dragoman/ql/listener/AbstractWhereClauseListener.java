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
package io.github.glytching.dragoman.ql.listener;

import com.google.common.collect.Lists;
import io.github.glytching.dragoman.antlr.SQLParser;
import io.github.glytching.dragoman.ql.domain.Predicate;
import io.github.glytching.dragoman.ql.listener.logging.LoggingListener;
import io.github.glytching.dragoman.ql.parser.WhereClauseParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.Collections;
import java.util.List;

/**
 * ANTLR hook for {@code where} expressions. Transforms a {@code where} expression into a collection
 * of {@link Predicate}. This should not be used directly, instead use a {@link WhereClauseParser}.
 * Note: this is stateful so we one of these per {@code where} expression but as long as the pattern
 * of using a {@link WhereClauseParser} is adhered to this statefulness issue is handled.
 *
 * @param <T> the target type, typically either Bson for a MongoDB source or String for a HTTP
 *     source
 */
public abstract class AbstractWhereClauseListener<T> extends LoggingListener {
    private static final String FIELD_SEPARATOR = ".";
    private static final String AND = "and";
    private static final String NOT = "not";
    // final results
    private final List<Predicate> predicates;
    // controllers
    private boolean inInClause;
    private boolean inNullClause;
    private boolean inBetweenClause;
    private boolean skipNextTerminal;
    // intermediate state
    private Predicate currentPredicate;
    private boolean negative;

    public AbstractWhereClauseListener() {
        this.predicates = Lists.newArrayList();
    }

    @Override
    public void enterPredicate(SQLParser.PredicateContext ctx) {
        super.enterPredicate(ctx);
        currentPredicate = new Predicate();
        negative = false;
    }

    @Override
    public void exitPredicate(SQLParser.PredicateContext ctx) {
        super.exitPredicate(ctx);
        predicates.add(currentPredicate);
    }

    @Override
    public void enterIdentifier(SQLParser.IdentifierContext ctx) {
        super.enterIdentifier(ctx);
        currentPredicate.appendLhsPart(ctx.start.getText());
        skipNextTerminal = true;
    }

    @Override
    public void enterComp_op(SQLParser.Comp_opContext ctx) {
        super.enterComp_op(ctx);
        String text = ctx.getText();
        currentPredicate.setOperator(text);
        skipNextTerminal = true;
    }

    @Override
    public void enterSign(SQLParser.SignContext ctx) {
        super.enterSign(ctx);
        if ("-".equals(ctx.start.getText())) {
            this.negative = true;
            skipNextTerminal = true;
        }
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);
        if (handleThisTerminal()) {
            String value = node.getText();
            if (!isSeparator(value)) {
                addRhs(value);
            }
        } else {
            skipNextTerminal = false;
        }
    }

    // START: LIKE
    @Override
    public void enterPattern_matcher(SQLParser.Pattern_matcherContext ctx) {
        super.enterPattern_matcher(ctx);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                if (isNot(child.getText())) {
                    currentPredicate.negate();
                }
                break;
            }
        }

        if (currentPredicate.isNegated()) {
            currentPredicate.setOperator("not like");
        } else {
            currentPredicate.setOperator("like");
        }
        skipNextTerminal = true;
    }

    @Override
    public void enterNegativable_matcher(SQLParser.Negativable_matcherContext ctx) {
        super.enterNegativable_matcher(ctx);
        skipNextTerminal = true;
    }
    // END: LIKE

    // START: IN
    @Override
    public void enterIn_predicate(SQLParser.In_predicateContext ctx) {
        super.enterIn_predicate(ctx);
        enterInClause();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                if (isNot(child.getText())) {
                    currentPredicate.negate();
                }
                break;
            }
        }
        currentPredicate.inOperator();
    }

    @Override
    public void enterIn_value_list(SQLParser.In_value_listContext ctx) {
        super.enterIn_value_list(ctx);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SQLParser.Row_value_predicandContext) {
                addRhs(child.getText());
            }
        }
    }

    @Override
    public void exitIn_predicate(SQLParser.In_predicateContext ctx) {
        super.exitIn_predicate(ctx);
        exitInClause();
    }
    // END: IN

    // START: NULL
    @Override
    public void enterNull_predicate(SQLParser.Null_predicateContext ctx) {
        super.enterNull_predicate(ctx);

        enterNullClause();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                if (isNot(child.getText())) {
                    currentPredicate.negate();
                }
            }
        }
        currentPredicate.nullOperator();
    }

    @Override
    public void exitNull_predicate(SQLParser.Null_predicateContext ctx) {
        super.exitNull_predicate(ctx);
        exitNullClause();
    }
    // END: NULL

    // START: BETWEEN
    @Override
    public void enterBetween_predicate_part_2(SQLParser.Between_predicate_part_2Context ctx) {
        super.enterBetween_predicate_part_2(ctx);
        enterBetweenClause();
        if (isNot(ctx.start.getText())) {
            currentPredicate.negate();
        }
    }

    @Override
    public void enterRow_value_predicand(SQLParser.Row_value_predicandContext ctx) {
        super.enterRow_value_predicand(ctx);
        if (inBetweenClause) {
            addRhs(ctx.getText());
        }
    }

    @Override
    public void exitBetween_predicate_part_2(SQLParser.Between_predicate_part_2Context ctx) {
        super.exitBetween_predicate_part_2(ctx);
        currentPredicate.betweenOperator();
        exitBetweenClause();
    }
    // END: BETWEEN

    public abstract T get();

    public List<Predicate> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }

    private void addRhs(String value) {
        currentPredicate.addRhs(negative ? "-" + value : value);
    }

    private boolean isNot(String text) {
        return NOT.equalsIgnoreCase(text);
    }

    private boolean isSeparator(String value) {
        return FIELD_SEPARATOR.equals(value) || AND.equals(value);
    }

    private boolean handleThisTerminal() {
        return !skipNextTerminal && !inInClause && !inNullClause && !inBetweenClause;
    }

    private void enterInClause() {
        this.inInClause = true;
    }

    private void exitInClause() {
        this.inInClause = false;
    }

    private void enterNullClause() {
        inNullClause = true;
    }

    private void exitNullClause() {
        inNullClause = false;
    }

    private void enterBetweenClause() {
        inBetweenClause = true;
    }

    private void exitBetweenClause() {
        inBetweenClause = false;
    }
}
