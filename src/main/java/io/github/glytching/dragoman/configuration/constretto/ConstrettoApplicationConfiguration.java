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
package io.github.glytching.dragoman.configuration.constretto;

import io.github.glytching.dragoman.configuration.ApplicationConfiguration;
import org.constretto.ConstrettoConfiguration;

import java.util.concurrent.Callable;

import static io.github.glytching.dragoman.util.NetworkUtils.getFreePort;
import static java.lang.String.format;

/**
 * An implementation of {@link ApplicationConfiguration} which uses {@code Constretto} to load
 * properties from the application's property file.
 */
public class ConstrettoApplicationConfiguration implements ApplicationConfiguration {
    private final ConstrettoConfiguration constretto;

    private Integer httpPort;
    private Integer mongoPort;
    private Integer jolokiaPort;

    public ConstrettoApplicationConfiguration(ConstrettoConfiguration constretto) {
        this.constretto = constretto;
    }

    @Override
    public int getHttpPort() {
        if (httpPort == null) {
            httpPort = getOrDefault("http.port");
        }
        return httpPort;
    }

    @Override
    public String getMongoHost() {
        return get(() -> constretto.evaluateToString("mongo.host"));
    }

    @Override
    public int getMongoPort() {
        if (mongoPort == null) {
            mongoPort = getOrDefault("mongo.port");
        }
        return mongoPort;
    }

    @Override
    public int getMongoServerSelectionTimeout() {
        return get(() -> constretto.evaluateToInt("mongo.server.selection.timeout.millis"));
    }

    @Override
    public int getMongoSocketConnectionTimeout() {
        return get(() -> constretto.evaluateToInt("mongo.socket.connection.timeout.millis"));
    }

    @Override
    public int getMongoReadTimeout() {
        return get(() -> constretto.evaluateToInt("mongo.read.timeout.millis"));
    }

    @Override
    public int getConnectionPoolMinSize() {
        return get(() -> constretto.evaluateToInt("connection.pool.min.size"));
    }

    @Override
    public int getConnectionPoolMaxSize() {
        return get(() -> constretto.evaluateToInt("connection.pool.max.size"));
    }

    @Override
    public int getConnectionPoolMaxWaitTime() {
        return get(() -> constretto.evaluateToInt("connection.pool.wait.timeout.millis"));
    }

    @Override
    public String getCannedUserName() {
        return get(() -> constretto.evaluateToString("canned.user.name"));
    }

    @Override
    public String getDatabaseName() {
        return get(() -> constretto.evaluateToString("database.name"));
    }

    @Override
    public String getDatasetStorageName() {
        return get(() -> constretto.evaluateToString("dataset.storage.name"));
    }

    @Override
    public String getUserStorageName() {
        return get(() -> constretto.evaluateToString("user.storage.name"));
    }

    @Override
    public boolean isMetricsEnabled() {
        return get(() -> constretto.evaluateToBoolean("metrics.enabled"));
    }

    @Override
    public boolean isJmxEnabled() {
        return get(() -> constretto.evaluateToBoolean("jmx.enabled"));
    }

    @Override
    public String getJmxDomainName() {
        return get(() -> constretto.evaluateToString("jmx.domain.name"));
    }

    @Override
    public boolean isJolokiaEnabled() {
        return get(() -> constretto.evaluateToBoolean("jolokia.enabled"));
    }

    @Override
    public int getJolokiaPort() {
        if (jolokiaPort == null) {
            jolokiaPort = getOrDefault("jolokia.port");
        }
        return jolokiaPort;
    }

    @Override
    public boolean isJolokiaDebugEnabled() {
        return get(() -> constretto.evaluateToBoolean("jolokia.debug.enabled"));
    }

    @Override
    public int getViewTemplateCacheSize() {
        return get(() -> constretto.evaluateToInt("view.template.cache.size"));
    }

    @Override
    public boolean isViewStaticAssetsCacheEnabled() {
        return get(() -> constretto.evaluateToBoolean("view.static.assets.cache.enabled"));
    }

    @Override
    public long getMaxEventLoopExecutionTime() {
        return get(() -> constretto.evaluateToLong("vertx.max.event.loop.execution.time.nanos"));
    }

    @Override
    public long getMaxWorkerExecutionTime() {
        return get(() -> constretto.evaluateToLong("vertx.max.worker.execution.time.nanos"));
    }

    @Override
    public int getWorkerPoolSize() {
        return get(() -> constretto.evaluateToInt("vertx.worker.pool.size"));
    }

    @Override
    public boolean isAuthenticationEnabled() {
        return get(() -> constretto.evaluateToBoolean("authentication.enabled"));
    }

    @Override
    public boolean isMongoEmbedded() {
        return get(() -> constretto.evaluateToBoolean("mongo.embedded"));
    }

    @Override
    public int getMetricsPublicationPeriod() {
        return get(() -> constretto.evaluateToInt("metrics.publication.period.in.millis"));
    }

    @Override
    public String getCannedDatasetsDirectory() {
        return get(() -> constretto.evaluateToString("canned.datasets.directory"));
    }

    @Override
    public <T> T getPropertyValue(Class<T> clazz, String propertyName) {
        return get(() -> constretto.evaluateTo(clazz, propertyName));
    }

    private <T> T get(Callable<T> c) {
        try {
            return c.call();
        } catch (Exception ex) {
            throw new RuntimeException(
                    format("Failed to read configuration property: %s!", ex.getMessage()));
        }
    }

    private int getOrDefault(String propertyName) {
        if (isRandomPortSymbolic(propertyName)) {
            return getFreePort();
        } else {
            return get(() -> constretto.evaluateToInt(propertyName));
        }
    }

    private boolean isRandomPortSymbolic(String propertyName) {
        return !constretto.hasValue(propertyName)
                || constretto.evaluateToInt(propertyName) == RANDOM_PORT_SYMBOLIC;
    }
}
