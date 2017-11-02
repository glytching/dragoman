package org.glitch.dragoman.transform;

public interface Transformer<F> {

    F transform(Object from);

    <T> T transform(Class<T> clazz, F from);
}