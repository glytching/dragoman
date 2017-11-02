package org.glitch.dragoman.repository.router;

import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.repository.Repository;

import java.util.Map;

public interface RepositoryRouter {

    Repository<Map<String, Object>> get(Dataset dataset);
}
