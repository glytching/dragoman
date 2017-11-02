package org.glitch.dragoman.store.http.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ListAwareResponsePostProcessor implements ResponsePostProcessor {

    private final ResponsePostProcessor delegate;

    public ListAwareResponsePostProcessor() {
        this(new NoOpResponsePostProcessor());
    }

    public ListAwareResponsePostProcessor(ResponsePostProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public String postProcess(String content) {
        String postProcessed = delegate.postProcess(content);

        if (isNotBlank(postProcessed)) {
            if (!postProcessed.startsWith("[")) {
                postProcessed = "[" + postProcessed + "]";
            }
        }

        return postProcessed;
    }
}
