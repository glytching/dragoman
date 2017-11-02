package org.glitch.dragoman.ql.listener.groovy;

/**
 * Interface which defines the filter feature for dynamically generated Groovy classes.
 */
public interface Filter {

    /**
     * Assess whether the given {@code object} matches the terms of this filter
     *
     * @param incoming an object to be filtered
     *
     * @return true if the given {@code object} matches the filter
     */
    boolean filter(Object incoming);
}