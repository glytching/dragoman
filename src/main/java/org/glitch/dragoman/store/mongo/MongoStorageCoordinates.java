package org.glitch.dragoman.store.mongo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MongoStorageCoordinates {

    private final String databaseName;
    private final String collectionName;

    public MongoStorageCoordinates(String encodedSource) {
        String[] split = encodedSource.split(":");
        this.databaseName = split[0];
        this.collectionName = split[1];
    }

    public MongoStorageCoordinates(String databaseName, String collectionName) {
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
