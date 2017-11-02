package org.glitch.dragoman.ql.listener;

import com.google.common.collect.Lists;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.domain.Projection;
import org.glitch.dragoman.ql.listener.logging.LoggingListener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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