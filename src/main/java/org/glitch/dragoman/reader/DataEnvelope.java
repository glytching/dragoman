package org.glitch.dragoman.reader;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class DataEnvelope {

    private final String source;
    private final Map<String, Object> payload;

    public DataEnvelope(String source, Map<String, Object> payload) {
        this.source = source;
        this.payload = payload;
    }

    public String getSource() {
        return source;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
