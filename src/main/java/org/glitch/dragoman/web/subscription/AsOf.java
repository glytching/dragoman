package org.glitch.dragoman.web.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AsOf {
    private static final Logger logger = LoggerFactory.getLogger(AsOf.class);

    private final String asOfField;
    private final String asOfFieldPattern;
    private final AsOfFormatter asOfFormatter;
    private LocalDateTime lastRead;

    AsOf(String asOfField, String asOfFieldPattern, LocalDateTime lastRead, AsOfFormatter asOfFormatter) {
        this.asOfField = asOfField;
        this.asOfFormatter = asOfFormatter;
        this.asOfFieldPattern = asOfFormatter.validate(asOfFieldPattern);
        this.lastRead = lastRead;
    }

    public String applyAsOf(String where) {
        String asOfPredicate = asOfField + " > " + asOfFormatter.format(asOfFieldPattern, lastRead);

        // increment the asOf for the next time around
        this.lastRead = LocalDateTime.now();

        logger.info("Derived asOf predicate: {}", asOfPredicate);
        return isBlank(where) ? asOfPredicate : (where + " and " + asOfPredicate);
    }
}
