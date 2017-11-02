package org.glitch.dragoman.web.subscription;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AsOfFormatter {
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public String format(String pattern, LocalDateTime lastRead) {
        if (isNotBlank(pattern)) {
            if (isDatePattern(pattern)) {
                return "'" + DateTimeFormatter.ofPattern(pattern).format(lastRead) + "'";
            } else if (isEpochMillisPattern(pattern)) {
                return String.valueOf(lastRead.toInstant(ZoneOffset.UTC).toEpochMilli());
            } else {
                throw new RuntimeException(String.format("Cannot format AsOf for an unsupported pattern: %s!", pattern));
            }
        } else {
            return "'" + DateTimeFormatter.ofPattern(ISO_8601).format(lastRead) + "'";
        }
    }

    public String validate(String pattern) {
        if (isNotBlank(pattern)) {
            if (isDatePattern(pattern) || isEpochMillisPattern(pattern)) {
                return pattern;
            } else {
                throw new RuntimeException(String.format("Cannot format AsOf for an unsupported pattern: %s!", pattern));
            }
        } else {
            return pattern;
        }
    }

    private boolean isEpochMillisPattern(String pattern) {
        return "L".equalsIgnoreCase(pattern);
    }

    /**
     * Any pattern which starts with {@code yyyy} (case insensitive) is deemed to be a date pattern. This sounds crude
     * and if <i>actual</i> usage shows it to be crude then it can be replaced with something more nuanced such as a
     * regex matcher.
     *
     * @param incoming
     *
     * @return
     */
    private boolean isDatePattern(String incoming) {
        return incoming.toLowerCase().startsWith("yyyy");
    }
}