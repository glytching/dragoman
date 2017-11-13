package org.glytching.dragoman.util.extension;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(EmbeddedEnvironmentExtension.class)
public class EmbeddedEnvironmentExtensionTest {

    @AfterAll
    public static void willUnsetEnvSystemPropertyAfterExecution() {
        assertThat(System.getProperty("env"), nullValue());
    }

    @Test
    public void willSetEnvSystemProperty() {
        assertThat(System.getProperty("env"), is("embedded"));
    }
}
