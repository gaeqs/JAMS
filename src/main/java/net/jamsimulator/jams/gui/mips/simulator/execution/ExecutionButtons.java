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
import javafx.scene.image.Image;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.util.FixedButton;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * General implementations of the execution buttons shown in a simulation pane.
 * <p>
 * Implemented buttons:
 * <li>Run step</li>
 * <li>Run all / Stop</li>
 * <li>Undo</li>
 * <li>Reset</li>
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
        Image runOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_PLAY_ONE).orElse(null);
        Image undoOneIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_UNDO_ONE).orElse(null);
        Image resetIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_RESET).orElse(null);

        runOrStop = new FixedButton("", new NearestImageView(null, 16, 16), 28, 28);
        changeToRunAll(simulation);

        runOne = new FixedButton("", new NearestImageView(runOneIcon, 16, 16), 28, 28);
        runOne.getStyleClass().add(STYLE_CLASS);
        runOne.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_EXECUTE_ONE, LanguageTooltip.DEFAULT_DELAY));
        runOne.setOnAction(event -> simulation.executeOneStep());


        Button undo = new FixedButton("", new NearestImageView(undoOneIcon, 16, 16), 28, 28);
        undo.getStyleClass().add(STYLE_CLASS);
        undo.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_UNDO, LanguageTooltip.DEFAULT_DELAY));
        undo.setOnAction(event -> simulation.undoLastStep());
        undo.setDisable(!simulation.isUndoEnabled());

        Button reset = new FixedButton("", new NearestImageView(resetIcon, 16, 16), 28, 28);
        reset.getStyleClass().add(STYLE_CLASS);

        reset.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_RESET, LanguageTooltip.DEFAULT_DELAY));
        reset.setOnAction(event -> simulation.reset());

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
            Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_PLAY
            ).orElse(null);
            ((NearestImageView) runOrStop.getGraphic()).setImage(runAllIcon);
            runOrStop.getStyleClass().add(STYLE_CLASS);
            runOrStop.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_EXECUTE_ALL, LanguageTooltip.DEFAULT_DELAY));
            runOrStop.setOnAction(event -> simulation.executeAll());
        });
    }

    private void changeToStop(MIPSSimulation<?> simulation) {
        Platform.runLater(() -> {
            Image runAllIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_STOP
            ).orElse(null);

            ((NearestImageView) runOrStop.getGraphic()).setImage(runAllIcon);
            runOrStop.getStyleClass().add(STYLE_CLASS);
            runOrStop.setTooltip(new LanguageTooltip(Messages.SIMULATION_BUTTON_TOOLTIP_STOP, LanguageTooltip.DEFAULT_DELAY));
            runOrStop.setOnAction(event -> simulation.stop());
        });
    }

}
