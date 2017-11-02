package org.glitch.dragoman.web;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.glitch.dragoman.web.exception.AccessDeniedException;
import org.glitch.dragoman.web.exception.InvalidCredentialsException;
import org.glitch.dragoman.web.exception.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Implements the global exception handling strategy for this app's HTTP API. All exceptions encountered by the
 * controller layer are routed via this handler and this handler applies the mapping from 'application exception type'
 * to HTTP status. The following HTTP statuses may be emitted by this handler
 * <ul>
 * <li>200 - OK</li>
 * <li>404 - Not Found</li>
 * <li>500 - Internal Server Error</li>
 * <li>400 - Bad Request</li>
 * <li>401 - Unauthorized</li>
 * <li>403 - Forbidden</li>
 * </ul>
 */
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static void error(RoutingContext routingContext) {
        Exception exception = (Exception) routingContext.failure();

        if (exception != null) {
            error(routingContext, exception);
        } else {
            // this is odd
            logger.warn("Routing context is failed but it does not contain an exception: {}!", routingContext);
        }
    }

    public static void error(RoutingContext routingContext, Throwable exception) {
        final JsonObject error = createErrorResponse(routingContext, exception);

        String encodedError = error.encode();
        logger.info("Caught exception at the end of the route chain: {}", encodedError);
        routingContext.response().setStatusCode(error.getInteger("statusCode")).end(encodedError);
    }

    //    public static void error(HttpServerResponse response, JsonObject errorResponse) {
    //        String encoded = errorResponse.encode();
    //
    //        logger.info("Caught exception at the end of the route chain: {}", encoded);
    //
    //        response.setStatusCode(errorResponse.getInteger("statusCode")).end(encoded);
    //    }

    private static JsonObject createErrorResponse(RoutingContext routingContext, Throwable exception) {
        // by default ...
        int status = 500;

        // intercept specific exception types and assign the relevant HTTP status code
        if (InvalidRequestException.class.isAssignableFrom(exception.getClass())) {
            status = HttpResponseStatus.BAD_REQUEST.code();
        } else if (InvalidCredentialsException.class.isAssignableFrom(exception.getClass())) {
            status = HttpResponseStatus.UNAUTHORIZED.code();
        } else if (AccessDeniedException.class.isAssignableFrom(exception.getClass())) {
            status = HttpResponseStatus.FORBIDDEN.code();
        }

        return new JsonObject()
                .put("timestamp", LocalDateTime.now().toString())
                .put("statusCode", status)
                .put("statusMessage", HttpResponseStatus.valueOf(status).reasonPhrase())
                .put("path", routingContext.request().uri())
                .put("message", orDefault(exception.getMessage(), ""))
                .put("stackTrace", displayable(exception.getStackTrace()));
    }

    @SuppressWarnings("SameParameterValue")
    private static String orDefault(String message, String defaultValue) {
        return isBlank(message) ? defaultValue : message;
    }

    private static String displayable(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(Objects::toString)
                .collect(Collectors.joining("\n"));
    }
}