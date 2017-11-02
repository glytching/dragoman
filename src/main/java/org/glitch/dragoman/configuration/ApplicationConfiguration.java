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
package org.glitch.dragoman.configuration;

public interface ApplicationConfiguration {
    int RANDOM_PORT_SYMBOLIC = -1;

    int getHttpPort();

    String getMongoHost();

    int getMongoPort();

    int getMongoServerSelectionTimeout();

    int getMongoSocketConnectionTimeout();

    int getMongoReadTimeout();

    int getConnectionPoolMinSize();

    int getConnectionPoolMaxSize();

    int getConnectionPoolMaxWaitTime();

    String getCannedUserName();

    String getDatabaseName();

    String getDatasetStorageName();

    String getUserStorageName();

    boolean isMetricsEnabled();

    long getMaxEventLoopExecutionTime();

    long getMaxWorkerExecutionTime();

    int getWorkerPoolSize();

    boolean isAuthenticationEnabled();

    boolean isMongoEmbedded();

    int getMetricsPublicationPeriod();

    String getCannedDatasetsDirectory();

    <T> T getPropertyValue(Class<T> clazz, String propertyName);
}