package org.glitch.dragoman.repository.router;

import org.glitch.dragoman.dataset.Dataset;

import static java.lang.String.format;

public class NoRepositoryAvailableException extends RuntimeException {

    public NoRepositoryAvailableException(String message, Throwable ex) {
        super(message, ex);
    }

    public NoRepositoryAvailableException(Dataset dataset) {
        super(format("No repository exists for dataset: %s", dataset.getId()));
    }
}