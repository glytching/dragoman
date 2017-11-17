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
package io.github.glytching.dragoman.dataset.canned;

import java.util.List;

/**
 * Defines how {@link CannedDataset} instances are loaded into memory, It is expected that these
 * instances are written to the dataset store as soon as they are loaded.
 */
public interface CannedDatasetsLoader {

    /**
     * Load a collection of {@link CannedDataset} instances.
     *
     * @param rootAddress the root address for the serialised representations of the {@link
     * CannedDataset} instances
     *
     * @return deserialised {@link CannedDataset} representations
     */
    List<CannedDataset> load(String rootAddress);
}
