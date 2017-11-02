package org.glitch.dragoman.ql.listener.groovy;

import java.util.Map;

/**
 * Interface which defines the map feature for dynamically generated Groovy classes.
 */
public interface Mapper {

    /**
     * Applies a selection function to the given {@code object}.
     *
     * @param incoming the object to be transformed
     *
     * @return the transformed object
     */
    Map<String, Object> map(Object incoming);
}
