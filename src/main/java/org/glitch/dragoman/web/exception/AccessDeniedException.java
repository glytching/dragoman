package org.glitch.dragoman.web.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("There is no user currently logged in!");
    }
}