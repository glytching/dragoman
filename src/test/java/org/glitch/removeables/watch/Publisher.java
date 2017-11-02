package org.glitch.removeables.watch;

public interface Publisher<T> {

    void publish(T publishable);

    void publish(Throwable throwable);
}
