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
package org.glytching.dragoman.repository.router;

import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.repository.Repository;

import java.util.Map;

/**
 * Routes datasets to repository. This application is configured with a set of {@link Repository} instances, these are
 * singletons and each {@link Dataset} can be associated with {@code 0..1} {@link Repository} instance. This router is
 * responsible for finding the correct {@link Repository} instance for a given {@link Dataset}.
 */
public interface RepositoryRouter {

    /**
     * Chooses a {@link Repository} implementation for the given {@code dataset}.
     *
     * @param dataset the {@link Dataset} for which a repository is to be provided
     *
     * @return a {@link Repository} implementation for the given {@code dataset} if one exists, an exception otherwise
     */
    Repository<Map<String, Object>> get(Dataset dataset);
}
