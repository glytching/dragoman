package org.glitch.removeables.watch;

import org.glitch.dragoman.ql.listener.groovy.Filter;
import org.glitch.dragoman.ql.listener.groovy.Mapper;

public interface DynamicInspectorFactory {

    Mapper createProjector(String selectClause);

    Filter createFilter(String whereClause);
}