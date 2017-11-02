package org.glitch.dragoman.reader;

import org.glitch.dragoman.dataset.Dataset;
import rx.Observable;

public interface Reader {

    Observable<DataEnvelope> read(Dataset dataset, String select, String where, String orderBy, Integer maxResults);
}