package org.glitch.removeables.simulator;

import org.bson.Document;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class DocumentSimulatedEventFactory implements SimulatedEventFactory<Document> {

    private final Random randomNumbers;

    public DocumentSimulatedEventFactory() {
        randomNumbers = new Random();
    }

    @Override
    public Document create() {
        return new Document("id", UUID.randomUUID().toString())
                .append("name", "Name" + anyInt())
                .append("age", anInt())
                .append("updatedAt", new Date());
        //                .append("updatedAt", LocalDateTime.now());
    }

    private int anInt() {
        // middle aged people only!
        return randomNumbers.nextInt(65 - 48) + 48;
    }

    private int anyInt() {
        return randomNumbers.nextInt(1000000);
    }
}
