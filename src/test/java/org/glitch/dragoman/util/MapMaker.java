package org.glitch.dragoman.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapMaker {

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static Map<String, Object> makeMap(AbstractMap.SimpleEntry<String, Object>... entries) {
        return Stream.of(
                entries).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public static AbstractMap.SimpleEntry<String, Object> makeEntry(String key, Object value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}