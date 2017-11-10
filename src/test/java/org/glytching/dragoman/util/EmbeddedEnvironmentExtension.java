package org.glytching.dragoman.util;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EmbeddedEnvironmentExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        System.clearProperty("env");
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        System.setProperty("env", "embedded");
    }
}
