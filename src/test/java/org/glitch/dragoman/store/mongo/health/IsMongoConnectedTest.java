package org.glitch.dragoman.store.mongo.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.rx.client.MongoClient;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IsMongoConnectedTest {

    @Mock
    private MongoProvider mongoProvider;
    @Mock
    private MongoClient mongoClient;

    private String host;
    private int port;

    private IsMongoConnected isMongoConnected;

    @Before
    public void setUp() {
        isMongoConnected = new IsMongoConnected(mongoProvider);

        host = "ahost";
        port = 1234;
        when(mongoProvider.provide()).thenReturn(mongoClient);
        when(mongoClient.getSettings()).thenReturn(mongoSettings(host, port));
    }

    @Test
    public void isHealthyIfWeCanTalkToMongo() throws Exception {
        HealthCheck.Result result = isMongoConnected.check();

        assertThat(result.isHealthy(), is(true));
        assertThat(result.getMessage(), is("Connected to MongoDB at " + host + ":" + port));

        verify(mongoClient, times(1)).getDatabase(anyString());
    }

    @Test
    public void isUnhealthyIfWeCannotTalkToMongo() throws Exception {
        when(mongoClient.getDatabase(anyString())).thenThrow(new RuntimeException());

        HealthCheck.Result result = isMongoConnected.check();

        assertThat(result.isHealthy(), is(false));
        assertThat(result.getMessage(), is("Cannot connect to MongoDB at " + host + ":" + port));
    }

    private MongoClientSettings mongoSettings(String host, int port) {
        return MongoClientSettings.builder()
                .clusterSettings(ClusterSettings.builder().hosts(newArrayList(new ServerAddress(host, port))).build())
                .build();
    }
}