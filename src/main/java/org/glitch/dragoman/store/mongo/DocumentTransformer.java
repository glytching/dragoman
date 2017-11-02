package org.glitch.dragoman.store.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.glitch.dragoman.transform.Transformer;
import org.glitch.dragoman.transform.TransformerException;

import javax.inject.Inject;

public class DocumentTransformer implements Transformer<Document> {

    private final ObjectMapper objectMapper;

    @Inject
    public DocumentTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Document transform(Object from) {
        try {
            if (!(from instanceof String)) {
                return Document.parse(objectMapper.writeValueAsString(from));
            } else {
                return Document.parse((String) from);
            }
        } catch (Exception ex) {
            throw new TransformerException("Failed to transform content to Document!", ex);
        }
    }

    public <T> T transform(Class<T> clazz, Document document) {
        try {
            document.remove("_id");
            return objectMapper.readValue(document.toJson(), clazz);
        } catch (Exception e) {
            throw new TransformerException("Failed to deserialise document!", e);
        }
    }
}