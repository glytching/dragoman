package io.github.glytching.dragoman.ql.listener.groovy;

/**
 * Extends {@link RuntimeException} to provide some identity and traceability for exceptions which
 * arise from generating Groovy projectors and filters.
 */
public class GroovyFactoryException extends RuntimeException {

  public GroovyFactoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
