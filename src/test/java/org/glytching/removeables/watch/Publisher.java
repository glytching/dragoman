package org.glytching.removeables.watch;

public interface Publisher<T> {

    void publish(T publishable);

    void publish(Throwable throwable);
}
