/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glytching.dragoman.dataset.canned;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.glytching.dragoman.transform.JsonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * An implementation of {@link CannedDatasetsLoader} which reads {@link CannedDataset}s from the classpath.
 */
public class CannedDatasetsLoaderImpl implements CannedDatasetsLoader {
    private static final Logger logger = LoggerFactory.getLogger(CannedDatasetsLoaderImpl.class);
    private static final String TABLE_OF_CONTENTS = "table-of-contents.txt";

    private final JsonTransformer jsonTransformer;
    private final Charset encoding = Charset.forName("UTF-8");

    @Inject
    public CannedDatasetsLoaderImpl(JsonTransformer jsonTransformer) {
        this.jsonTransformer = jsonTransformer;
    }

    /**
     * Loads {@link CannedDataset}s from the classpath with the given {@code rootAddress} being the address from which
     * to load them. It is expected that the {@code rootAddress} will contains a file named
     * {@code table-of-contents.txt} and this file will list the files to be loaded. Each file represents a single
     * {@link CannedDataset}. This might sounds odd; why not just interrogate a folder and load the files found therein?
     * Well, that's not a runner when the canned dataset resources are bundled inside the application JAR. Since we
     * have to cater for the dataset resources beingbundled inside the application JAR we use this table-of-contents
     * approach.
     *
     * @param rootAddress the root address for the serialised representations of the {@link CannedDataset} instances
     *
     * @return deserialised {@link CannedDataset} representations
     */
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