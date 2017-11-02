package org.glitch.dragoman.store.http.repository;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListAwareResponsePostProcessorTest {

    @Test
    public void willWrapAResponseInAnArray() {
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor();

        String actual = responsePostProcessor.postProcess("{\"a\": \"b\"}, {\"c\": 1}");

        assertThat(actual, is("[{\"a\": \"b\"}, {\"c\": 1}]"));
    }

    @Test
    public void willDoNothingIfTheResponseIsAlreadyAnArray() {
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor();

        String actual = responsePostProcessor.postProcess("[{\"a\": \"b\"}, {\"c\": 1}]");

        assertThat(actual, is("[{\"a\": \"b\"}, {\"c\": 1}]"));
    }

    @Test
    public void willDoNothingIfTheResponseIsEmpty() {
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor();

        String actual = responsePostProcessor.postProcess("");

        assertThat(actual, is(""));
    }

    @Test
    public void willDelegateToTheWrappedProcessor() {
        ResponsePostProcessor delegate = mock(ResponsePostProcessor.class);
        ResponsePostProcessor responsePostProcessor = new ListAwareResponsePostProcessor(delegate);

        String content = "{\"a\": \"b\"}, {\"c\": 1}";
        when(delegate.postProcess(content)).thenReturn(content);

        String actual = responsePostProcessor.postProcess(content);

        assertThat(actual, is("[{\"a\": \"b\"}, {\"c\": 1}]"));
    }
}
