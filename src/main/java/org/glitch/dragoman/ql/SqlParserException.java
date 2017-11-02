package org.glitch.dragoman.ql;

public class SqlParserException extends RuntimeException {
    public SqlParserException(String message) {
        super(message);
    }

    public SqlParserException(String message, Exception exception) {
        super(message, exception);
    }
}
