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

/**
 * Defines the interaction with our dataset store for canned datasets. Implementations of this interface are expected
 * to be specific to a chosen data store technology e.g. MongoDB, a RDBMS etc.
 */
public interface CannedDatasetsWriter {

    /**
     * Write to the configured dataset store.
     *
     * @return the number of datasets written
     */
    int write();
}
