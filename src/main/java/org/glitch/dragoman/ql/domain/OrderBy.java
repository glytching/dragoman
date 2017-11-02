package org.glitch.dragoman.ql.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OrderBy {
    private final String name;
    private final boolean ascending;

    public OrderBy(String name, boolean ascending) {
        this.name = name;
        this.ascending = ascending;
    }

    public String getName() {
        return name;
    }

    public boolean isAscending() {
        return ascending;
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
        return name + " " + (isAscending() ? "asc" : "desc");
    }
}