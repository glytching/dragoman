package org.glitch.dragoman.ql.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.bson.conversions.Bson;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.listener.AbstractWhereClauseListener;
import org.glitch.dragoman.ql.listener.groovy.GroovyWhereClauseListener;
import org.glitch.dragoman.ql.listener.mongo.MongoWhereClauseListener;

import static java.lang.String.format;

public class WhereClauseParser extends BaseParser {

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
