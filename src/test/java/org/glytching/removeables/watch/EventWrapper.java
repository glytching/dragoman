package org.glytching.removeables.watch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class EventWrapper {

    private final String source;
    private final Map<String, Object> event;

    EventWrapper(String source, Map<String, Object> event) {
        this.source = source;
        this.event = event;
    }

    String getSource() {
        return source;
    }

    public Map<String, Object> getEvent() {
        return event;
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
