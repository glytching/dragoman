package org.glitch.removeables.simulator;

import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.functions.Function;

public class RxMapWrapper {
    public static <T, R> Func1<T, R> wrapAndThrow(Func1Wrapper<T, R> caughtFunction) {
        return t -> {
            try {
                return caughtFunction.call(t);
            } catch (Exception e) {
                throw Exceptions.propagate(e);
            }
        };
    }

    public interface Func1Wrapper<T, R> extends Function {
        R call(T t) throws Exception;
    }
}