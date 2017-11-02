package org.glitch.dragoman.ql.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Projection {
    private String name;

    public Projection(String name) {
        this.name = name;
    }

    public void appendNamePart(String namePart) {
        this.name = this.name + '.' + namePart;
    }

    public String getName() {
        return name;
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
        return this.name;
    }
}