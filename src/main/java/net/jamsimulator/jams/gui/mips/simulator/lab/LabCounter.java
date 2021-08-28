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

package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.configuration.RegionDisplay;
import net.jamsimulator.jams.gui.util.value.RangedIntegerValueEditor;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationCycleEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;

public class LabCounter extends VBox {

    public static final String CONFIG_NODE = "simulation.mips.lab_counter_address";

    private final MIPSSimulation<?> simulation;
    private final RangedIntegerValueEditor numberEditor;
    private final ProgressBar progressBar;
    private final Label counterDisplay;

    public int address;
    private byte counter, reset;

    public LabCounter(MIPSSimulation<?> simulation) {
        this.simulation = simulation;
        this.address = Jams.getMainConfiguration().getNumber(CONFIG_NODE).orElse(0).intValue();

        getChildren().add(new RegionDisplay(Messages.LAB_COUNTER));
        setSpacing(5);
        setFillWidth(true);

        numberEditor = new RangedIntegerValueEditor();
        progressBar = new ProgressBar(1.0);
        counterDisplay = new Label("0");

        loadEditor();
        loadProgressBar();

        simulation.registerListeners(this, true);
        simulation.getMemory().registerListeners(this, true);
        Jams.getMainConfiguration().registerListeners(this, true);
    }

    private void loadEditor() {
        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);

        counter = reset = simulation.getMemory().getByte(address);

        numberEditor.setMin(0);
        numberEditor.setMax(255);
        numberEditor.setCurrentValue(Byte.toUnsignedInt(counter));

        var button = new LanguageButton(Messages.LAB_COUNTER_RESET);
        hbox.getChildren().addAll(numberEditor, button);

        numberEditor.prefWidthProperty().bind(hbox.widthProperty().multiply(0.65));
        button.prefWidthProperty().bind(hbox.widthProperty().multiply(0.35));
        getChildren().add(hbox);

        button.setOnAction(event -> simulation.runSynchronized(() -> {
            counter = reset = numberEditor.getCurrentValue().byteValue();
            progressBar.setProgress(1.0);
            simulation.getMemory().setByte(address, counter);
        }));
    }

    private void loadProgressBar() {
        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);

        progressBar.prefWidthProperty().bind(hbox.widthProperty().multiply(0.85));
        hbox.getChildren().addAll(counterDisplay, progressBar);
        getChildren().add(hbox);
    }

    private void refershProgressBar() {
        double dCounter = Byte.toUnsignedInt(counter);
        double dReset = Byte.toUnsignedInt(reset);
        progressBar.setProgress(reset == 0 ? 1.0 : dCounter / dReset);
    }

    @Listener
    private void onWordSet(MemoryWordSetEvent.After event) {
        int wordAddress = address >> 2 << 2;
        if (wordAddress == event.getAddress()) {
            int offset = address - wordAddress;
            int value = (event.getValue() >> offset * 8) & 0xFF;
            counter = reset = (byte) value;
            Platform.runLater(() -> {
                numberEditor.setCurrentValue(value);
                progressBar.setProgress(1.0);
                counterDisplay.setText(String.valueOf(Byte.toUnsignedInt(counter)));
            });
        }
    }

    @Listener
    private void onByteSet(MemoryByteSetEvent.After event) {
        if (address == event.getAddress()) {
            counter = reset = event.getValue();
            Platform.runLater(() -> {
                numberEditor.setCurrentValue(Byte.toUnsignedInt(counter));
                progressBar.setProgress(1.0);
                counterDisplay.setText(String.valueOf(Byte.toUnsignedInt(counter)));
            });
        }
    }

    @Listener(priority = Integer.MIN_VALUE)
    private void onSimulationCycle(SimulationCycleEvent.Before event) {
        if (reset == 0) return;
        if (--counter == 0) {
            counter = reset;
            simulation.runSynchronized(() -> simulation.requestHardwareInterrupt(2));
        }
        if (simulation.getCycleDelay() > 0) {
            Platform.runLater(() -> {
                refershProgressBar();
                counterDisplay.setText(String.valueOf(Byte.toUnsignedInt(counter)));
            });
        }
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        Platform.runLater(() -> {
            refershProgressBar();
            counterDisplay.setText(String.valueOf(Byte.toUnsignedInt(counter)));
        });
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        Platform.runLater(() -> {
            progressBar.setProgress(1.0);
            counterDisplay.setText("0");
        });
        counter = reset = 0;
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent.After event) {
        if (reset == 0) return;
        if (counter == reset) {
            counter = 1;
        } else {
            counter++;
        }
        Platform.runLater(() -> {
            refershProgressBar();
            counterDisplay.setText(String.valueOf(Byte.toUnsignedInt(counter)));
        });
    }

    @Listener
    private void onNodeChange(ConfigurationNodeChangeEvent.After event) {
        if (event.getNode().equals(CONFIG_NODE)) {
            var number = (Number) event.getNewValueAs().orElse(0);
            address = number.intValue();
        }
    }

}
