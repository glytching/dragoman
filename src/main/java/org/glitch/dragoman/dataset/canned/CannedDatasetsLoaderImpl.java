package org.glitch.dragoman.dataset.canned;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.glitch.dragoman.transform.JsonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

public class CannedDatasetsLoaderImpl implements CannedDatasetsLoader {
    private static final Logger logger = LoggerFactory.getLogger(CannedDatasetsLoaderImpl.class);
    private static final String TABLE_OF_CONTENTS = "table-of-contents.txt";

    private final JsonTransformer jsonTransformer;
    private final Charset encoding = Charset.forName("UTF-8");

    @Inject
    public CannedDatasetsLoaderImpl(JsonTransformer jsonTransformer) {
        this.jsonTransformer = jsonTransformer;
    }

    @Override
    public List<CannedDataset> load(String rootAddress) {
        List<CannedDataset> cannedDatasets = Lists.newArrayList();

        try {
            logger.info("Looking for canned datasets from: {}", rootAddress + "/table-of-contents.txt");

            InputStream resourceAsStream = this.getClass().getResourceAsStream(rootAddress + "/" + TABLE_OF_CONTENTS);
            if (resourceAsStream != null) {
                List<String> tableOfContents = IOUtils.readLines(resourceAsStream, Charset.forName("UTF-8"));

                logger.info("Loading canned datasets: {}", Joiner.on(",").join(tableOfContents));

                for (String cannedDatasetFileName : tableOfContents) {
                    Optional<CannedDataset> cannedDataset =
                            loadCannedDataset(rootAddress + "/" + cannedDatasetFileName);
                    if (cannedDataset.isPresent()) {
                        cannedDatasets.add(cannedDataset.get());
                    }
                }
            } else {
                logger.warn("A canned datasets directory ({}) was supplied but it does not contain {} so no canned " +
                        "datasets can be loaded!", rootAddress, TABLE_OF_CONTENTS);
            }

            logger.info("Loaded {} canned datasets from {}", cannedDatasets.size(), rootAddress);

            return cannedDatasets;
        } catch (Exception ex) {
            throw new RuntimeException(format("Failed to read files from: %s", rootAddress), ex);
        }
    }

    private Optional<CannedDataset> loadCannedDataset(String address) throws IOException {
        logger.info("Loading canned dataset from: {}", address);

        InputStream resourceAsStream = this.getClass().getResourceAsStream(address);
        if (resourceAsStream != null) {
            CannedDataset cannedDataset =
                    jsonTransformer.transform(CannedDataset.class, IOUtils.toString(resourceAsStream, encoding));

            return Optional.of(cannedDataset.finish());
        } else {
            logger.info("No such file exists: {}!", address);
            return Optional.empty();
        }
    }
}