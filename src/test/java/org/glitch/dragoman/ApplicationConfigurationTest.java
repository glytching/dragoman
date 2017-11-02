package org.glitch.dragoman;

import org.constretto.ConstrettoBuilder;
import org.constretto.model.ClassPathResource;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.configuration.constretto.ConstrettoApplicationConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationConfigurationTest {

    static {
        System.setProperty("env", "test");
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private ApplicationConfiguration configuration;

    @Before
    public void setUp() {
        configuration = new ConstrettoApplicationConfiguration(new ConstrettoBuilder()
                .addCurrentTag(System.getProperty("env", ""))
                .createPropertiesStore()
                .addResource(new ClassPathResource("test-application.properties"))
                .done()
                .getConfiguration());
    }

    @Test
    public void canReadAnEnvSpecificProperty() {
        // the test case is running with the 'test' TAG
        assertThat(configuration.getHttpPort(), is(9101112));
    }

    @Test
    public void canDeriveAValueForTheRandomPortSymbolic() {
        // we cannot know exactly what value will be produced but it suffices to know that it is > 0 since
        // this means that a value in a valid port range has been derived
        assertThat(configuration.getMongoPort(), greaterThan(0));
    }

    @Test
    public void canReadAStringProperty() {
        assertThat(configuration.getMongoHost(), is("someHost"));
    }

    @Test
    public void canReadAnIntPropertyValue() {
        assertThat(configuration.getConnectionPoolMinSize(), is(5));
    }

    @Test
    public void canReadABooleanPropertyValue() {
        assertThat(configuration.isMetricsEnabled(), is(true));
    }

    @Test
    public void cannotReadANonExistentProperty() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Failed to read configuration property: Expression [foo] not found");

        configuration.getPropertyValue(String.class, "foo");
    }
}
