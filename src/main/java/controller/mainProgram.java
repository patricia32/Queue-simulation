package controller;

import view.SimulationFrame;

public class mainProgram {

    public static void main(String[] args) {
        SimulationFrame view = new SimulationFrame();
        view.setVisible(true);
        SimulationManager simulationManager = new SimulationManager(view);
        simulationManager = simulationManager.startRun(simulationManager);
    }
}
