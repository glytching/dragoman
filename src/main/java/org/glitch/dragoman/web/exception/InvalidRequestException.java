package org.glitch.dragoman.web.exception;

import static java.lang.String.format;

public class InvalidRequestException extends RuntimeException {
    public static InvalidRequestException missingParameter(String parameterName) {
        return new InvalidRequestException(format("The parameter: %s must be supplied!", parameterName));
    }

    public static InvalidRequestException invalidBody(String request, String targetType) {
        return new InvalidRequestException(format("Failed to deserialise request body: %s to type: %s!", request,
                targetType));
    }

    private InvalidRequestException(String message) {
        super(message);
    }

    public static InvalidRequestException invalidValue(String parameterName, String parameterValue,
                                                       String expectedValuePattern) {
        return new InvalidRequestException(format("Invalid value: %s for parameter: %s, valid values are: %s!",
                parameterValue, parameterName, expectedValuePattern));
    }

    public static InvalidRequestException create(String message) {
        return new InvalidRequestException(message);
    }
}