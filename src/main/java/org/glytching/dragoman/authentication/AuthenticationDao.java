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
package org.glytching.dragoman.authentication;

/**
 * Defines the interaction with our authentication store. Implementations of this interface are expected to be specific
 * to a chosen data store technology e.g. MongoDB, a RDBMS etc.
 */
public interface AuthenticationDao {

    /**
     * Does an entry exist for the given {@code userName}?
     *
     * @param userName the name of a (proposed) user
     *
     * @return true if an entry exists in the authentication store for the given {@code userName}, false otherwise
     */
    boolean exists(String userName);

    /**
     * Does the given combination of {@code userName} and {@code password} match an entry in the authentication store?
     *
     * @param userName the name of a user
     * @param password the password for the {@code userName}
     *
     * @return true if an entry exists in the authentication store for the given combination of {@code userName} and
     * {@code password}, false otherwise
     */
    boolean isValid(String userName, String password);

    /**
     * Retrieve the {@link User} record from the authentication store for the given combination of {@code userName}
     * and {@code password}.
     *
     * @param userName the name of a user
     * @param password the password for the {@code userName}
     *
     * @return the {@link User} record if an entry exists in the authentication store for the given combination of
     * {@code userName} and {@code password}, null otherwise
     */
    User getUser(String userName, String password);

    /**
     * Create a {@link User} record in the authentication store for the given combination of {@code userName}
     * and {@code password}.
     *
     * @param userName the name of a user
     * @param password the password for the {@code userName}
     */
    void createUser(String userName, String password);
}