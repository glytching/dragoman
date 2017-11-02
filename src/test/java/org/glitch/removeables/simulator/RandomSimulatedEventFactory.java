package org.glitch.removeables.simulator;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;

public class RandomSimulatedEventFactory implements SimulatedEventFactory<String> {

    // TODO start with simple string substitution but move onto Thymeleaf
    // and add support for symbolics such as objectId (replaced by UUID)
    // maybe types too (doubles, ints, dates etc)

    private static final String TEMPLATE = "{\"id\": \"%s\", \"name\": \"%s\", \"age\": %s}";

    private final RandomStringGenerator randomStrings;
    private final Random randomNumbers;

    public RandomSimulatedEventFactory() {
        randomStrings = new RandomStringGenerator.Builder().filteredBy(CharacterPredicates.LETTERS).build();

        randomNumbers = new Random();
    }

    @Override
    public String create() {
        return format(TEMPLATE, UUID.randomUUID().toString(), aString(), anInt());
    }

    private String aString() {
        return randomStrings.generate(5);
    }

    private int anInt() {
        // middle aged people only!
        return randomNumbers.nextInt(65 - 48) + 48;
    }
}
