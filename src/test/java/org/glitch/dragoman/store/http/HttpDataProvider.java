package org.glitch.dragoman.store.http;

import java.util.List;
import java.util.Map;

public interface HttpDataProvider {

    List<Map<String, Object>> getAll();
}