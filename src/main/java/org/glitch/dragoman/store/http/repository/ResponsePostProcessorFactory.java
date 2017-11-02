package org.glitch.dragoman.store.http.repository;

import org.glitch.dragoman.dataset.Dataset;

public class ResponsePostProcessorFactory {
    private static final ResponsePostProcessor DEFAULT_RESPONSE_POST_PROCESSOR = new ListAwareResponsePostProcessor();

    /**
     * Returns an instance of {@link ResponsePostProcessor} appropriate to the given {@code dataset}. Currently, there
     * is only implementation of {@link ResponsePostProcessor} so this factory looks a little bit silly.
     *
     * @param dataset
     *
     * @return
     */
    public ResponsePostProcessor create(@SuppressWarnings("unused") Dataset dataset) {
        return DEFAULT_RESPONSE_POST_PROCESSOR;
    }
}
