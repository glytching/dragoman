package org.glitch.dragoman.util;

import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class UrlUtils {

    private final UrlValidator urlValidator;

    public UrlUtils() {
        this.urlValidator = new UrlValidator(new RegexValidator(".*"), 0L);
    }

    public boolean isUrl(String incoming) {
        return urlValidator.isValid(incoming);
    }
}