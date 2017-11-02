package org.glitch.dragoman.ql.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.bson.conversions.Bson;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.ql.listener.AbstractOrderByClauseListener;
import org.glitch.dragoman.ql.listener.mongo.MongoOrderByClauseListener;

import static java.lang.String.format;

public class OrderByClauseParser extends BaseParser {

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