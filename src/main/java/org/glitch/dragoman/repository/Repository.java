package org.glitch.dragoman.repository;

import org.glitch.dragoman.dataset.Dataset;
import rx.Observable;

public interface Repository<T> {

    Observable<T> find(Dataset dataset, String select, String where, String orderBy, int maxResults);

    boolean appliesTo(Dataset dataset);
}
