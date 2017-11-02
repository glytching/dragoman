package org.glitch.dragoman.transform;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;

public class JsonTransformer implements Transformer<String> {

    private final ObjectMapper objectMapper;

    @Inject
    public JsonTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String transform(Object from) {
        try {
            return objectMapper.writeValueAsString(from);
        } catch (Exception ex) {
            throw new TransformerException("Failed to serialise content!", ex);
        }
    }

    @Override
    public <T> T transform(Class<T> clazz, String from) {
        try {
            return objectMapper.readValue(from, clazz);
        } catch (Exception ex) {
            throw new TransformerException("Failed to deserialise content!", ex);
        }
    }
}