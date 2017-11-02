package org.glitch.dragoman.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.constretto.ConstrettoBuilder;
import org.constretto.model.ClassPathResource;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.configuration.constretto.ConstrettoApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ConfigurationModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationModule.class);

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public ApplicationConfiguration provideApplicationConfiguration() {
        String env = System.getProperty("env", "");

        if (isNotBlank(env)) {
            logger.info("Configuring system for env={}", env);
        }

        return new ConstrettoApplicationConfiguration(new ConstrettoBuilder()
                .addCurrentTag(env)
                .createPropertiesStore()
                .addResource(new ClassPathResource("application.properties"))
                .done()
                .getConfiguration());
    }
}