package org.glitch.dragoman.dataset.canned;

import java.util.List;

public interface CannedDatasetsLoader {

    List<CannedDataset> load(String rootAddress);
}