package org.glitch.dragoman.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glitch.dragoman.transform.Transformer;
import org.glitch.dragoman.transform.TransformerException;
import org.glitch.dragoman.web.exception.InvalidRequestException;

import javax.inject.Inject;

public class ViewTransformer implements Transformer<String> {

    private final ObjectMapper objectMapper;

    @Inject
    public ViewTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String transform(Object from) {
        try {
            return objectMapper.writeValueAsString(from);
        } catch (Exception ex) {
            throw new TransformerException("Failed to serialise response content!", ex);
        }
    }

    @Override
    public <T> T transform(Class<T> clazz, String from) {
        try {
            return objectMapper.readValue(from, clazz);
        } catch (Exception ex) {
            throw InvalidRequestException.invalidBody(from, clazz.getSimpleName());
        }
    }
}