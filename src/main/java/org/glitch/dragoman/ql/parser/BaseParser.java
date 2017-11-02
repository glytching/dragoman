package org.glitch.dragoman.ql.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.glitch.dragoman.antlr.SQLLexer;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.antlr.SQLParserListener;
import org.glitch.dragoman.ql.SqlParserException;
import org.glitch.dragoman.ql.listener.ErrorListener;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class BaseParser {

    /**
     * This is ANTLR's recommended two stage parsing strategy:
     * <ol>
     * <li>Parse with {@link PredictionMode#SLL} and if this fails then move on to ...</li>
     * <li>Parse with {@link PredictionMode#LL}</li>
     * </ol>
     */
    private static final PredictionMode DEFAULT_PREDICTION_MODE = PredictionMode.SLL;

    protected void parse(String expression, SQLParserListener sqlParserListener) {
        if (isNotBlank(expression)) {
            try {
                // stateful listener so use a fresh one each time through the parse call
                ErrorListener errorListener = new ErrorListener();

                SQLParser parser = getSqlParser(expression, errorListener);
                ParserRuleContext entryPoint = getParserContext(parser);

                if (errorListener.hasErrors()) {
                    // continue but with a stricter mode
                    entryPoint = withStrictMode(parser).select_list();
                }

                parse(sqlParserListener, errorListener, entryPoint);
            } catch (Exception e) {
                // catching the general Exception because the listener may have thrown a runtime
                throw new SqlParserException(e.getMessage(), e);
            }
        }
    }

    /**
     * The parser call involves:
     * <ol>
     * <li>Creating a parser for a specific clause such as map or where etc</li>
     * <li>Creating a listener for a specific type of output such as MongoDb, Groovy etc</li>
     * <li>Invoking the parser with the listener such that the derived output is contained within the listener
     * after the parse call completes.</li>
     * </ol>
     * This method provides an extension point which allows clause-specific parsers a run with a selected listener and
     * get the listeners contents. It is just sugar which allows a caller to write ...
     * <p>
     * <pre>
     *     DerivedResponse derivedResponse = parser.get(DerivedResponse.class, expression);
     * </pre>
     * <p>
     * ... instead of:
     * <p>
     * <pre>
     *     SomeKindOfListener aListener = new SomeKindOfListener();
     *     parser.parse(expression, aListener);
     *     DerivedResponse derivedResponse = aListener.getDerivedResponse();
     * </pre>
     *
     * @param clazz
     * @param expression
     * @param <T>
     *
     * @return
     */
    public abstract <T> T get(Class<T> clazz, String expression);

    /**
     * Extension point which allows clause-specific parsers to define the entry point for their own clause. For
     * example an {@code orderBy} parser has a different entry point to a {@code map} parser.
     *
     * @param parser
     *
     * @return a {@link ParserRuleContext} representing the correct point in the tree for a given type of parser
     */
    protected abstract ParserRuleContext getParserContext(SQLParser parser);


    private SQLParser getSqlParser(String expression, ErrorListener errorListener) {
        SQLLexer lexer = new SQLLexer(new ANTLRInputStream(expression));

        SQLParser sqlParser = new SQLParser(new CommonTokenStream(lexer));
        sqlParser.addErrorListener(errorListener);
        sqlParser.getInterpreter().setPredictionMode(DEFAULT_PREDICTION_MODE);

        return sqlParser;
    }

    private SQLParser withStrictMode(SQLParser parser) {
        // if the faster (more lenient) mode fails then retry with the slower (stricter) mode
        parser.getInterpreter().setPredictionMode(PredictionMode.LL);

        return parser;
    }

    private void parse(SQLParserListener sqlParserListener, ErrorListener errorListener,
                       ParserRuleContext entryPoint) {
        ParseTreeWalker walker = new ParseTreeWalker();

        walker.walk(sqlParserListener, entryPoint);

        // if the parser call raised any exceptions then lets throw
        errorListener.getException().ifPresent(ex -> {
            throw ex;
        });
    }
}