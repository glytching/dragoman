package org.glitch.removeables.simulator;

public class SimulatorBuilder {

    private SimulatorDelay simulatorDelay;
    private SimulatedEventFactory simulatedEventFactory;

    public SimulatorBuilder withDelay(SimulatorDelay simulatorDelay) {
        this.simulatorDelay = simulatorDelay;
        return this;
    }

    public SimulatorBuilder withEventCreator(SimulatedEventFactory simulatedEventFactory) {
        this.simulatedEventFactory = simulatedEventFactory;
        return this;
    }

    public JsonSimulator buildJsonSimulator() {
        if (simulatorDelay == null) {
            simulatorDelay = new NoOpSimulatorDelay();
        }
        if (simulatedEventFactory == null) {
            simulatedEventFactory = new RandomSimulatedEventFactory();
        }

        return new JsonSimulator(simulatorDelay, simulatedEventFactory);
    }

    public DocumentSimulator buildDocumentSimulator() {
        if (simulatorDelay == null) {
            simulatorDelay = new NoOpSimulatorDelay();
        }
        if (simulatedEventFactory == null) {
            simulatedEventFactory = new RandomSimulatedEventFactory();
        }

        return new DocumentSimulator(simulatorDelay, new DocumentSimulatedEventFactory());
    }
}
