package org.glytching.removeables;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.GroovyClassLoader;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.ql.listener.groovy.Filter;
import org.glytching.dragoman.ql.listener.groovy.GroovyFactory;
import org.glytching.dragoman.ql.listener.groovy.GroovyFactoryException;
import org.glytching.dragoman.ql.listener.groovy.Mapper;
import org.glytching.dragoman.ql.parser.SelectClauseParser;
import org.glytching.dragoman.ql.parser.WhereClauseParser;
import org.glytching.dragoman.repository.Repository;
import org.glytching.removeables.simulator.Simulator;
import rx.Observable;
import rx.functions.Func1;

import java.io.IOException;
import java.util.Map;

public class SimulatedRepository implements Repository<Map<String, Object>> {

    private final Simulator<String> simulator;
    private final GroovyFactory groovyFactory;
    private final ObjectMapper objectMapper;

    public SimulatedRepository(Simulator<String> simulator) {
        this.simulator = simulator;
        this.groovyFactory = new GroovyFactory(new GroovyClassLoader(), new SelectClauseParser(), new WhereClauseParser());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Observable<Map<String, Object>> find(Dataset dataset, String select, String where, String orderBy,
                                                int maxResults) {
        try {
            Mapper mapper = groovyFactory.createProjector("age");
            Filter filter = groovyFactory.createFilter("age > 60");

            return simulator.simulate(maxResults)
                    .map(toMap())
                    .filter(filter::filter)
                    .map(mapper::map);
        } catch (GroovyFactoryException ex) {
            // ??
        }

        return null;
    }

    @Override
    public boolean appliesTo(Dataset dataset) {
        return dataset.getSource().startsWith("http://");
    }

    @SuppressWarnings("unchecked")
    private Func1<String, Map<String, Object>> toMap() {
        return s -> {
            try {
                // transform to Map<String, Object>
                return objectMapper.readValue(s, Map.class);
            } catch (IOException e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e);
            }
        };
    }
}