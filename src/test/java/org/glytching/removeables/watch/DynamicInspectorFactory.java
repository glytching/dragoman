package org.glytching.removeables.watch;

import org.glytching.dragoman.ql.listener.groovy.Filter;
import org.glytching.dragoman.ql.listener.groovy.Mapper;

public interface DynamicInspectorFactory {

    Mapper createProjector(String selectClause);

    Filter createFilter(String whereClause);
}