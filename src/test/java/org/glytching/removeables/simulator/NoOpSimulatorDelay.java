package org.glytching.removeables.simulator;

public class NoOpSimulatorDelay implements SimulatorDelay {

    @Override
    public void delay(int index) {
        // no-op
    }
}