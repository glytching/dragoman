package org.glitch.removeables.simulator;

public class EveryNElementSimulatorDelay implements SimulatorDelay {

    private final int n;
    private final int delayPeriodMillis;

    public EveryNElementSimulatorDelay(int n, int delayPeriodMillis) {
        this.n = n;
        this.delayPeriodMillis = delayPeriodMillis;
    }

    @Override
    public void delay(int index) {
        if (index % n == 0) {
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(delayPeriodMillis);
        } catch (InterruptedException e) {
            // no-op
        }
    }
}
