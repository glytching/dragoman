package org.glitch.dragoman.util;

import org.apache.commons.text.RandomStringGenerator;

public class RandomValues {

    private static final RandomStringGenerator randomStrings = new RandomStringGenerator.Builder().build();

    public static String aString() {
        return randomStrings.generate(5);
    }
}
