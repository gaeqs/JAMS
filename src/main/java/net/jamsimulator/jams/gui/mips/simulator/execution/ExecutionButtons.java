/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
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

package net.jamsimulator.jams.gui.mips.simulator.execution;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * General implementations of the execution buttons shown in a simulation pane.
 * <p>
 * Implemented buttons:
 * <ul>
 * <li>Run step</li>
 * <li>Run all / Stop</li>
 * <li>Undo</li>
 * <li>Reset</li>
 * </ul>
 */
public class ExecutionButtons {

    /**
     * The style class used by the buttons.
     */
    public static final String STYLE_CLASS = "buttons-hbox-button";

    private final Button runOrStop;
    private final Button runOne;
    private final List<Node> nodes;

    public ExecutionButtons(Simulation<?> simulation) {
        nodes = new ArrayList<>();
        var runOneIcon = Icons.SIMULATION_PLAY_ONE;
        var undoOneIcon = Icons.SIMULATION_UNDO_ONE;
        var resetIcon = Icons.SIMULATION_RESET;

        runOrStop = new Button("", new QualityImageView(null, 16, 16));
        runOrStop.getStyleClass().add(STYLE_CLASS);
        changeToRunAll(simulation);

        runOne = new Button("", new QualityImageView(runOneIcon, 16, 16));
        runOne.getStyleClass().add(STYLE_CLASS);
        runOne.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_EXECUTE_ONE, LanguageTooltip.DEFAULT_DELAY));
        runOne.setOnAction(event -> simulation.executeOneStep());


        Button undo = new Button("", new QualityImageView(undoOneIcon, 16, 16));
        undo.getStyleClass().add(STYLE_CLASS);
        undo.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_UNDO, LanguageTooltip.DEFAULT_DELAY));
        undo.setOnAction(event -> {
            try {
                simulation.undoLastStep();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        undo.setDisable(!simulation.isUndoEnabled());

        Button reset = new Button("", new QualityImageView(resetIcon, 16, 16));
        reset.getStyleClass().add(STYLE_CLASS);

        reset.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_RESET, LanguageTooltip.DEFAULT_DELAY));
        reset.setOnAction(event -> {
            try {
                simulation.reset();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        nodes.add(runOrStop);
        nodes.add(runOne);
        nodes.add(undo);
        nodes.add(reset);

        simulation.registerListeners(this, true);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Listener
    private void onSimulationStart(SimulationStartEvent event) {
        runOne.setDisable(true);
        changeToStop(event.getSimulation());
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        runOne.setDisable(false);
        changeToRunAll(event.getSimulation());
    }

    private void changeToRunAll(Simulation<?> simulation) {
        Platform.runLater(() -> {
            ((QualityImageView) runOrStop.getGraphic()).setIcon(Icons.SIMULATION_PLAY);
            runOrStop.getStyleClass().add(STYLE_CLASS);
            runOrStop.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_EXECUTE_ALL, LanguageTooltip.DEFAULT_DELAY));
            runOrStop.setOnAction(event -> simulation.executeAll());
        });
    }

    private void changeToStop(Simulation<?> simulation) {
        Platform.runLater(() -> {
            ((QualityImageView) runOrStop.getGraphic()).setIcon(Icons.SIMULATION_STOP);
            runOrStop.getStyleClass().add(STYLE_CLASS);
            runOrStop.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_STOP, LanguageTooltip.DEFAULT_DELAY));
            runOrStop.setOnAction(event -> simulation.stop());
        });
    }

}
