package org.glitch.dragoman.web.subscription;

import javax.inject.Inject;
import java.time.LocalDateTime;

public class AsOfFactory {
    private final AsOfFormatter asOfFormatter;

    @Inject
    public AsOfFactory(AsOfFormatter asOfFormatter) {
        this.asOfFormatter = asOfFormatter;
    }

    public AsOf create(String asOfField, String asOfFieldPattern, LocalDateTime lastRead) {
        return new AsOf(asOfField, asOfFieldPattern, lastRead, asOfFormatter);
    }
}
