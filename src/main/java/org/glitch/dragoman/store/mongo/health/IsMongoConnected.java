package org.glitch.dragoman.store.mongo.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.ServerAddress;
import com.mongodb.rx.client.MongoClient;
import org.glitch.dragoman.store.mongo.MongoProvider;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class IsMongoConnected extends HealthCheck {

    private final MongoProvider mongoProvider;

    @Inject
    public IsMongoConnected(MongoProvider mongoProvider) {
        this.mongoProvider = mongoProvider;
    }

    @Override
    protected Result check() throws Exception {
        MongoClient mongoClient = mongoProvider.provide();

        List<ServerAddress> serverAddresses = mongoClient.getSettings().getClusterSettings().getHosts();
        String address = serverAddresses
                .stream()
                .map(ServerAddress::toString)
                .collect(Collectors.joining(","));

        try {
            // any read will suffice to prove connectivity
            mongoClient.getDatabase("xyz");
            return Result.healthy("Connected to MongoDB at " + address);
        } catch (Exception ex) {
            return Result.unhealthy("Cannot connect to MongoDB at " + address);
        }
    }
}
