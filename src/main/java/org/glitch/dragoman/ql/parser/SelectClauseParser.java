package org.glitch.dragoman.ql.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.bson.conversions.Bson;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.listener.AbstractSelectClauseListener;
import org.glitch.dragoman.ql.listener.groovy.GroovySelectClauseListener;
import org.glitch.dragoman.ql.listener.mongo.MongoSelectClauseListener;

import static java.lang.String.format;

public class SelectClauseParser extends BaseParser {

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