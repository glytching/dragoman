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
package io.github.glytching.dragoman.configuration;

/**
 * Defines the well known configuration properties for this application. Implementations of this interface are expected
 * to be specific to whatever library/technology is chosen to integrate with the external properties file.
 */
public interface ApplicationConfiguration {

    /**
     * A property which is defined with this value will be replaced by a generated 'free port' value on loading. Once
     * the {@link ApplicationConfiguration} has been laoded each subsequent call for that property will return the same
     * value. This is useful when the application nees to assign assign a free port on startup and then use that value
     * for the life of the application.
     */
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

    boolean isJmxEnabled();

    String getJmxDomainName();

    boolean isJolokiaEnabled();

    int getJolokiaPort();

    boolean isJolokiaDebugEnabled();

    int getViewTemplateCacheSize();

    boolean isViewStaticAssetsCacheEnabled();

    long getMaxEventLoopExecutionTime();

    long getMaxWorkerExecutionTime();

    int getWorkerPoolSize();

    boolean isAuthenticationEnabled();

    boolean isMongoEmbedded();

    int getMetricsPublicationPeriod();

    String getCannedDatasetsDirectory();

    /**
     * The non specific form of 'property getter'. Typically, the specific getters (e.g. {@link #isMongoEmbedded()},
     * {@link #getHttpPort()} etc) are facades over this method.
     *
     * @param clazz the expected type of the property value
     * @param propertyName the name of the property to be retrieved
     * @param <T>
     *
     * @return the value of the given {@code propertyName} coerced to the type {@code T} or an exception if no such
     * property exists.
     */
    <T> T getPropertyValue(Class<T> clazz, String propertyName);
}