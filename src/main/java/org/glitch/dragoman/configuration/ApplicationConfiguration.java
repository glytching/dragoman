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