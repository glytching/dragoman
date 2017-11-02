package org.glitch.dragoman.web;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServerUtils {
    private static final Logger logger = LoggerFactory.getLogger(WebServerUtils.class);

    private static final String USER_SESSION_KEY = "userName";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_PLAIN = "text/plain";
    private static final String ROOT_CONTEXT = "/dragoman/";

    public static String withApplicationName(String path) {
        return ROOT_CONTEXT + path;
    }

    public static void defaultContentType(Route route) {
        route.consumes(CONTENT_TYPE_APPLICATION_JSON).produces(CONTENT_TYPE_APPLICATION_JSON);
    }

    public static HttpServerResponse plainContentType(HttpServerResponse response) {
        return response.putHeader("content-type", CONTENT_TYPE_PLAIN);
    }

    public static HttpServerResponse jsonContentType(HttpServerResponse response) {
        return response.putHeader("content-type", CONTENT_TYPE_APPLICATION_JSON);
    }

    public static void assignUserToSession(Session session, String userName) {
        logger.info("Login for user: {}", userName);

        session.put(USER_SESSION_KEY, userName);
    }

    public static boolean isLoggedIn(Session session, String userName) {
        return userName.equals(session.get(USER_SESSION_KEY));
    }

    public static void removeUserFromSession(Session session) {
        logger.info("Logout for user: {}", getCurrentUserName(session));

        session.remove(USER_SESSION_KEY);
    }

    public static void assignUserToRoutingContext(RoutingContext routingContext) {
        // put the current user into the request data
        routingContext.data().put(USER_SESSION_KEY, getCurrentUserName(routingContext.session()));
    }

    public static String getCurrentUserName(Session session) {
        return session.get(USER_SESSION_KEY);
    }
}