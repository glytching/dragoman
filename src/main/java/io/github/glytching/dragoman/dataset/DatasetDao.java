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
package io.github.glytching.dragoman.dataset;

import rx.Observable;

/**
 * Defines the interaction with our dataset store. Implementations of this interface are expected to
 * be specific to a chosen data store technology e.g. MongoDB, a RDBMS etc.
 */
public interface DatasetDao {

  /**
   * Retrieves all {@link Dataset}s associated with the given {@code userName}.
   *
   * @param userName the name of the uer associated with 0..* datasets
   * @return an Observable over the {@link Dataset}s associated with the given {@code userName}
   */
  Observable<Dataset> getAll(String userName);

  /**
   * Gets the {@link Dataset} associated with the given {@code id}.
   *
   * @param id the unique identifier for a dataset
   * @return the {@link Dataset} associated with the given {@code id} or null if no such dataset
   *     exists
   */
  Dataset get(String id);

  /**
   * Is there a {@link Dataset} associated with the given {@code id}?
   *
   * @param id the unique identifier for a dataset
   * @return true if a {@link Dataset} exists for the given {@code id}, false otherwise
   */
  boolean exists(String id);

  /**
   * Delete the {@link Dataset} associated with the given {@code id}.
   *
   * @param id the unique identifier for a dataset
   * @return a count of the records deleted, since {@code id} is the unique identifier for a dataset
   *     this will be {@code 1} if a {@link Dataset} exists for the given {@code id}, {@code 0}
   *     otherwise
   */
  long delete(String id);

  /**
   * Writes the given {@code dataset} to the dataset store. If the given {@code Dataset} contains an
   * {@code id} which matches an existing dataset record then that record will be replaced,
   * otherwise a new record will be created.
   *
   * @param dataset the dataset entity
   * @return the written dataset, in the case of an insert this will contain the auto populated
   *     {@code id}, in the case of a replace this will be identical to the supplied {@code dataset}
   */
  Dataset write(Dataset dataset);
}
