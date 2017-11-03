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
package org.glitch.dragoman.transform;

/**
 * Defines the interaction with our transformer layer. Implementations of this interface are expected to be:
 * <ul>
 * <li>Specific to a chosen transformer technology e.g. Jackson, Gson etc</li>
 * <li>Specific to a purpose e.g. transforming for the view layer, transforming MongoDB BSON etc</li>
 * </ul>
 */
public interface Transformer<F> {

    /**
     * Transforms the given {@code from} into an instance of {@code F}.
     *
     * @param from
     *
     * @return
     */
    F transform(Object from);

    /**
     * Transforms the given {@code from} into something of type {@code clazz}.
     *
     * @param clazz the target type
     * @param from the object to be transformed
     * @param <T> the target type
     *
     * @return an instance of {@code T} representing {@code from}
     */
    <T> T transform(Class<T> clazz, F from);
}