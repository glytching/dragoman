package org.glitch.dragoman.ql.listener;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.glitch.dragoman.ql.SqlParserException;

import java.util.List;
import java.util.Optional;

public class ErrorListener extends BaseErrorListener {
    private final List<String> errorMessages = Lists.newArrayList();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int lineNumber, int positionInLine,
                            String message, RecognitionException e) {
        errorMessages.add(asMessage(lineNumber, positionInLine, message));
    }

    private String asMessage(int lineNumber, int positionInLine, String message) {
        return "Line: " + lineNumber + ", Position: " + positionInLine + ": " + message;
    }

    public Optional<SqlParserException> getException() {
        if (!errorMessages.isEmpty()) {
            Joiner joiner = Joiner.on(", ").skipNulls();
            return Optional.of(new SqlParserException(joiner.join(errorMessages)));
        }
        return Optional.empty();
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }
}