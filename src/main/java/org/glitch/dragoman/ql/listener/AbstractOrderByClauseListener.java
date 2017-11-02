package org.glitch.dragoman.ql.listener;

import com.google.common.collect.Lists;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.domain.OrderBy;
import org.glitch.dragoman.ql.listener.logging.LoggingListener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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