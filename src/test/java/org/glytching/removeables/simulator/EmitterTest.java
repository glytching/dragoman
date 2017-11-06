package org.glytching.removeables.simulator;

import org.junit.Ignore;

@Ignore
public class EmitterTest {
    //    public static void main(String[] args) {
    //        Observable<Integer> obs = Observable.fromEmitter(emitter -> {
    //            for (int i = 1; i < 1000; i++) {
    //                if (i % 100 == 0) {
    //                    sleep(1500L);
    //                }
    //
    //                emitter.onNext(i);
    //            }
    //
    //            emitter.onCompleted();
    //        }, Emitter.BackpressureMode.NONE);
    //
    //        obs
    //                //                .subscribeOn(Schedulers.computation())
    //                .onBackpressureLatest()
    //                //                .observeOn(Schedulers.computation())
    //                .subscribe(value -> System.out.println("Received " + value)); // Why does this get stuck at "Received 128"
    //
    //        sleep(1000000L);
    //    }
    //
    //    private static void sleep(Long duration) {
    //        try {
    //            Thread.sleep(duration);
    //        } catch (InterruptedException e) {
    //            throw new RuntimeException(e);
    //        }
    //    }
}
