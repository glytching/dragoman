package org.glytching.dragoman.util.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

public class RandomBeansExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (hasRandomAnnotation(field)) {
                Object randomObject = resolve(field, field.getType());

                field.setAccessible(true);
                field.set(testInstance, randomObject);
            }
        }
    }

    private Object resolve(AnnotatedElement annotatedElement, Class<?> targetType) {
        return random(targetType);
    }

    private boolean hasRandomAnnotation(AnnotatedElement annotatedElement) {
        return isAnnotated(annotatedElement, Random.class);
    }
}