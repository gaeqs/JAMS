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

package net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;

import java.util.LinkedList;

public class SingleCycleFlowTable extends FlowTable {

    private LinkedList<SingleCycleInstructionExecutionEvent.After> toAdd;

    public SingleCycleFlowTable(MIPSSimulation<? extends SingleCycleArchitecture> simulation) {
        super(simulation);

        if (simulation.canCallEvents()) {
            toAdd = new LinkedList<>();
            simulation.registerListeners(this, true);
        } else {
            flows.setAlignment(Pos.CENTER);
            var label = new LanguageLabel(Messages.SIMULATION_EVENTS_DISABLED);
            label.setAlignment(Pos.CENTER);
            flows.getChildren().add(label);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public MIPSSimulation<? extends SingleCycleArchitecture> getSimulation() {
        return (MIPSSimulation<? extends SingleCycleArchitecture>) super.getSimulation();
    }

    @Override
    public long getFirstCycle() {
        if (flows.getChildren().isEmpty()) return 0;
        return ((SingleCycleFlowEntry) flows.getChildren().get(0)).getCycle();
    }

    @Override
    public long getLastCycle() {
        if (flows.getChildren().isEmpty()) return 0;
        return getFirstCycle() + flows.getChildren().size() - 1;
    }

    @Listener
    private void onInstructionExecuted(SingleCycleInstructionExecutionEvent.After event) {
        //Adding items to a separate list prevents the app to block.
        if (toAdd.size() == maxItems) {
            toAdd.remove(0);
        }
        toAdd.add(event);
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        Platform.runLater(() -> {
            if (toAdd.size() >= maxItems) {
                flows.getChildren().clear();
            } else if (flows.getChildren().size() + toAdd.size() > maxItems) {
                flows.getChildren().remove(0, flows.getChildren().size() + toAdd.size() - maxItems);

                int index = 0;
                for (Node child : flows.getChildren()) {
                    if (child instanceof SingleCycleFlowEntry) {
                        ((SingleCycleFlowEntry) child).refresh(index++, stepSize);
                    }
                }
            }


            String start = String.valueOf(simulation.getRegisters()
                    .getValidRegistersStarts().stream().findAny().orElse('$'));
            SingleCycleInstructionExecutionEvent.After current;
            SingleCycleFlowEntry entry;
            while (!toAdd.isEmpty()) {
                current = toAdd.pop();
                entry = new SingleCycleFlowEntry(flows.getChildren().size(), this,
                        current.getInstruction().orElse(null), start, current.getCycle(), stepSize);
                flows.getChildren().add(flows.getChildren().size(), entry);
            }
            refreshVisualizer();
        });
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        flows.getChildren().clear();
        refreshVisualizer();
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent.After event) {
        Platform.runLater(() -> {
            flows.getChildren().remove(flows.getChildren().size() - 1);
            refreshVisualizer();
        });
    }

    @Override
    public void setStepSize(double stepSize) {
        super.setStepSize(stepSize);
        int index = 0;
        for (Node child : flows.getChildren()) {
            if (child instanceof SingleCycleFlowEntry) {
                ((SingleCycleFlowEntry) child).refresh(index++, stepSize);
            }
        }
    }
}
