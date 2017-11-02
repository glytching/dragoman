package org.glitch.dragoman.dataset;

import rx.Observable;

public interface DatasetDao {

    Observable<Dataset> getAll(String user);

    Dataset get(String id);

    boolean exists(String id);

    long delete(String id);

    Dataset write(Dataset dataset);
}