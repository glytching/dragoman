package org.glitch.dragoman.store.http.repository;

public class NoOpResponsePostProcessor implements ResponsePostProcessor {

    @Override
    public String postProcess(String content) {
        return content;
    }
}
