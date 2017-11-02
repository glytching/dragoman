package org.glitch.dragoman.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JsonTransformerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonTransformer documentTransformer;

    @Before
    public void setUp() {
        documentTransformer = new JsonTransformer(objectMapper);
    }

    @Test
    public void canTransformMapToJson() {
        Map<String, Object> from = new HashMap<>();
        from.put("a", "b");
        from.put("c", 1);

        String transformed = documentTransformer.transform(from);

        assertThat(transformed, is("{\"a\":\"b\",\"c\":1}"));
    }

    @Test
    public void canTransformJsonToMap() {
        String json = "{\"a\": \"b\", \"c\": 1}";

        //noinspection unchecked
        Map<String, Object> transformed = documentTransformer.transform(Map.class, json);

        assertThat(transformed.size(), is(2));

        assertThat(transformed, hasKey("a"));
        assertThat(transformed.get("a"), is("b"));
        assertThat(transformed, hasKey("c"));
        assertThat(transformed.get("c"), is(1));
    }

    @Test
    public void canPerformTwoWayTransform() {
        Map<String, Object> from = new HashMap<>();
        from.put("a", "b");
        from.put("c", 1);

        String json = documentTransformer.transform(from);

        //noinspection unchecked
        Map<String, Object> actual = documentTransformer.transform(Map.class, json);

        assertThat(actual, is(from));
    }
}