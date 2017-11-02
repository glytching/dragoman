package org.glitch.dragoman.ql.listener.groovy;

import com.google.common.collect.Maps;
import groovy.lang.GroovyClassLoader;
import org.glitch.dragoman.ql.SqlParserException;
import org.glitch.dragoman.ql.parser.SelectClauseParser;
import org.glitch.dragoman.ql.parser.WhereClauseParser;
import org.junit.Test;

import java.util.Map;

import static org.glitch.dragoman.util.MapMaker.makeEntry;
import static org.glitch.dragoman.util.MapMaker.makeMap;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GroovySelectClauseListenerTest {

    private final GroovyFactory factory = new GroovyFactory(new GroovyClassLoader(), new SelectClauseParser(), new WhereClauseParser());

    @Test
    public void testSingleProjection() throws Exception {
        Map<String, Object> document = makeMap(makeEntry("a", 1), makeEntry("b", 2));

        Map<String, Object> actual = project("b", document);

        assertThat(actual.size(), is(1));
        assertThat(actual, hasEntry("b", 2));
    }

    @Test
    public void testMultipleProjections() throws Exception {
        Map<String, Object> document = makeMap(
                makeEntry("a", 1),
                makeEntry("b", makeMap(makeEntry("c", 2), makeEntry("d", 3))),
                makeEntry("e", "hello")
        );

        Map<String, Object> actual = project("a, e, b.c", document);

        assertThat(actual.size(), is(3));
        assertThat(actual, hasEntry("a", 1));
        assertThat(actual, hasEntry("b.c", 2));
        assertThat(actual, hasEntry("e", "hello"));
    }

    @Test
    public void testStarProjection() throws Exception {
        Map<String, Object> document = makeMap(makeEntry("a", 1), makeEntry("b", 2));

        Map<String, Object> actual = project("*", document);

        assertThat(actual, is(document));
    }

    @Test
    public void testEmptyProjection() throws Exception {
        Map<String, Object> document = makeMap(makeEntry("a", 1), makeEntry("b", 2));

        Map<String, Object> actual = project("", document);

        assertThat(actual, is(document));
    }

    @Test
    public void testNullProjection() throws Exception {
        Map<String, Object> document = makeMap(makeEntry("a", 1), makeEntry("b", 2));

        Map<String, Object> actual = project(null, document);

        assertThat(actual, is(document));
    }

    @Test
    public void testUnmatchedProjection() throws Exception {
        Map<String, Object> document = makeMap(makeEntry("a", 1), makeEntry("b", 2));

        Map<String, Object> actual = project("c", document);

        assertThat(actual.size(), is(1));
        assertThat(actual, hasEntry("c", null));
    }

    @Test
    public void testProjectionsOnEmptyObject() throws Exception {
        Map<String, Object> document = makeMap();

        Map<String, Object> actual = project("a, b", document);

        assertThat(actual.size(), is(2));
        assertThat(actual, hasEntry("a", null));
        assertThat(actual, hasEntry("b", null));
    }

    @Test
    public void testStarProjectionOnEmptyObject() throws Exception {
        Map<String, Object> document = makeMap();

        Map<String, Object> actual = project("*", document);

        assertThat(actual, is(document));
    }

    @Test(expected = SqlParserException.class)
    public void testInvalidProjection() throws Exception {
        Map<String, Object> document = Maps.newHashMap();

        project("a, ", document);
    }

    private Map<String, Object> project(String select, Map<String, Object> document) throws Exception {
        Mapper mapper = factory.createProjector(select);

        return mapper.map(document);
    }
}