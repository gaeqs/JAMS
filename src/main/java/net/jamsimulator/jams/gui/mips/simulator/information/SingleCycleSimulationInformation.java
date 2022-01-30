/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.simulator.information;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.configuration.RegionDisplay;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;

public class SingleCycleSimulationInformation extends VBox {

    protected final Simulation<?> simulation;
    protected final LanguageLabel textCycles;
    protected final LanguageLabel textInsturctions;
    protected final LanguageLabel textExecutionTime;
    protected final LanguageLabel textCPI;
    protected final LanguageLabel textCyclesPerSecond;
    protected final LanguageLabel textInsturctionsPerSecond;

    public SingleCycleSimulationInformation(Simulation<?> simulation) {
        this.simulation = simulation;

        getStyleClass().add(SimulationInformationBuilder.STYLE_CLASS);

        textCycles = new LanguageLabel(Messages.SIMULATION_INFORMATION_CYCLES);
        textInsturctions = new LanguageLabel(Messages.SIMULATION_INFORMATION_INSTRUCTIONS);
        textExecutionTime = new LanguageLabel(Messages.SIMULATION_INFORMATION_EXECUTION_TIME);
        textCPI = new LanguageLabel(Messages.SIMULATION_INFORMATION_CPI);
        textCyclesPerSecond = new LanguageLabel(Messages.SIMULATION_INFORMATION_CYCLES_PER_SECOND);
        textInsturctionsPerSecond = new LanguageLabel(Messages.SIMULATION_INFORMATION_INSTRUCTIONS_PER_SECOND);

        getChildren().add(new RegionDisplay(Messages.SIMULATION_INFORMATION_SECTION_EXECUTION));
        getChildren().add(textCycles);
        getChildren().add(textInsturctions);
        getChildren().add(textExecutionTime);
        getChildren().add(textCPI);
        getChildren().add(textCyclesPerSecond);
        getChildren().add(textInsturctionsPerSecond);

        simulation.registerListeners(this, true);
        refreshValues();
    }

    public Simulation<?> getSimulation() {
        return simulation;
    }

    protected void refreshValues() {
        textCycles.setReplacements(new String[]{"{CYCLES}", String.valueOf(simulation.getCycles())});
        textInsturctions.setReplacements(new String[]{"{INSTRUCTIONS}", String.valueOf(simulation.getExecutedInstructions())});

        double executionTime = simulation.getExecutionTime() / 1_000_000_000.0;
        textExecutionTime.setReplacements(new String[]{"{TIME}", String.format("%.4f", executionTime)});

        double cpi = simulation.getCycles() / (double) simulation.getExecutedInstructions();
        textCPI.setReplacements(new String[]{"{CPI}", isValid(cpi) ? "-" : String.format("%.4f", cpi)});

        double cps = simulation.getCycles() / executionTime;
        double ips = simulation.getExecutedInstructions() / executionTime;
        textCyclesPerSecond.setReplacements(new String[]{"{CPS}", isValid(cps) ? "-" : String.format("%.4f", cps)});
        textInsturctionsPerSecond.setReplacements(new String[]{"{IPS}", isValid(ips) ? "-" : String.format("%.4f", ips)});
    }

    private static boolean isValid(double d) {
        return Double.isInfinite(d) || Double.isNaN(d);
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        Platform.runLater(this::refreshValues);
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent event) {
        Platform.runLater(this::refreshValues);
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        Platform.runLater(this::refreshValues);
    }

    public static final class Builder implements SimulationInformationBuilder {

        @Override
        public Node buildNewNode(Simulation<?> simulation) {
            return new SingleCycleSimulationInformation(simulation);
        }

        @Override
        public ResourceProvider getResourceProvider() {
            return ResourceProvider.JAMS;
        }

        @Override
        public String getName() {
            return SingleCycleArchitecture.NAME;
        }
    }


}
