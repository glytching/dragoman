package org.glitch.dragoman.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.web.exception.InvalidRequestException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.glitch.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class ViewTransformerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ViewTransformer documentTransformer;

    @Before
    public void setUp() {
        documentTransformer = new ViewTransformer(objectMapper);
    }

    @Test
    public void canPerformTwoWayTransform() {
        Dataset dataset = anyDataset();

        String json = documentTransformer.transform(dataset);

        Dataset actual = documentTransformer.transform(Dataset.class, json);

        assertThat(actual, is(dataset));
    }

    @Test
    public void willThrowAnInvalidRequestExceptionIfTheGivenJsonCannotBeDeserialised() {
        expectedException.expect(InvalidRequestException.class);
        expectedException.expectMessage(startsWith("Failed to deserialise request body"));

        String json = "{\"a\": \"b\", \"c\": 1}";

        documentTransformer.transform(Dataset.class, json);
    }
}