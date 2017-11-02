package org.glitch.dragoman.util;

import org.junit.rules.ExternalResource;

import java.util.LinkedHashMap;
import java.util.Map;

public class SystemPropertyRule extends ExternalResource {
    private final Map<String, String> properties = new LinkedHashMap<>();
    private final Map<String, String> restoreProperties = new LinkedHashMap<>();

    public SystemPropertyRule(String propertyName, String propertyValue) {
        this.properties.put(propertyName, propertyValue);
    }

    @Override
    protected void before() throws Throwable {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            String propertyValue = entry.getValue();
            String existingValue = System.getProperty(propertyName);
            if (existingValue != null) {
                // store the overriden value so that it can be reinstated when the test completes
                restoreProperties.put(propertyName, existingValue);
            }
            System.setProperty(propertyName, propertyValue);
        }
    }


    @Override
    protected void after() {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            String propertyValue = entry.getValue();
            if (restoreProperties.containsKey(propertyName)) {
                // reinstate the overridden value
                System.setProperty(propertyName, propertyValue);
            } else {
                // remove the (previously unset) property
                System.clearProperty(propertyName);
            }
        }
    }
}
