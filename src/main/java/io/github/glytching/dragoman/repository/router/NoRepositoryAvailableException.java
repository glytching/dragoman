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
package io.github.glytching.dragoman.repository.router;

import io.github.glytching.dragoman.dataset.Dataset;

import static java.lang.String.format;

/**
 * Extends {@link RuntimeException} to provide some identity and traceability for exceptions which
 * arise when a dataset cannot be routed to a repository.
 */
public class NoRepositoryAvailableException extends RuntimeException {

  public NoRepositoryAvailableException(String message, Throwable ex) {
    super(message, ex);
  }

  public NoRepositoryAvailableException(Dataset dataset) {
    super(format("No repository exists for dataset: %s", dataset.getId()));
  }
}
