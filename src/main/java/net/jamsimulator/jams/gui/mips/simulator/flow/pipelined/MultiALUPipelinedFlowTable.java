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

package net.jamsimulator.jams.gui.mips.simulator.flow.pipelined;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.SegmentedFlowEntry;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipeline;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelineSlot;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelineSlotStatus;
import net.jamsimulator.jams.mips.simulation.multialupipelined.event.MultiALUPipelineShiftEvent;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MultiALUPipelinedFlowTable extends FlowTable {

    private LinkedList<MultiALUPipelineShiftEvent> toAdd;
    private LinkedList<MultiALUPipeline> pipelines;
    private Map<Long, SegmentedFlowEntry> entries;

    private long firstCycle;

    public MultiALUPipelinedFlowTable(MIPSSimulation<? extends MultiCycleArchitecture> simulation) {
        super(simulation);

        firstCycle = 0;

        if (simulation.canCallEvents()) {
            toAdd = new LinkedList<>();
            pipelines = new LinkedList<>();
            entries = new HashMap<>();
            simulation.registerListeners(this, true);
        } else {
            flows.setAlignment(Pos.CENTER);
            flows.getChildren().add(new Label("Events are disabled."));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public MIPSSimulation<? extends MultiCycleArchitecture> getSimulation() {
        return (MIPSSimulation<? extends MultiCycleArchitecture>) super.getSimulation();
    }

    @Override
    public void setStepSize(double stepSize) {
        super.setStepSize(stepSize);
        refresh();
    }


    private void flushEvents() {
        if (toAdd.isEmpty()) return;
        String start = String.valueOf(simulation.getRegisters().getValidRegistersStarts()
                .stream().findAny().orElse('$'));

        while (!toAdd.isEmpty()) {
            var event = toAdd.pop();
            var pipeline = pipelines.pop();

            addStep(pipeline.getFetch(), MultiCycleStep.FETCH, start, event.getCycle(), false);
            addStep(pipeline.getDecode(), MultiCycleStep.DECODE, start, event.getCycle(), false);
            addStep(pipeline.getMemory(), MultiCycleStep.MEMORY, start, event.getCycle(), false);
            addStep(pipeline.getWriteback(), MultiCycleStep.WRITE_BACK, start, event.getCycle(), false);
            addStep(pipeline.getFinished(), MultiCycleStep.WRITE_BACK, start, event.getCycle(), true);
            pipeline.getExecute().forEach(ex -> addStep(ex, MultiCycleStep.EXECUTE, start, event.getCycle(), false));
        }

        long localCycle = firstCycle;
        firstCycle = flows.getChildren().isEmpty()
                ? 0
                : ((SegmentedFlowEntry) flows.getChildren().get(0)).getStartingCycle();

        if (localCycle != firstCycle) {
            refresh();
        }

        refreshVisualizer();
    }

    private void addStep(MultiALUPipelineSlot slot, MultiCycleStep step, String registerStart, long cycle, boolean isFinished) {
        if (slot == null || slot.execution == null) return;
        if(!isFinished && slot.status == MultiALUPipelineSlotStatus.EXECUTED) step = step.getPreviousStep();
        var entry = entries.get(slot.execution.getInstructionId());
        if (entry == null) {
            entry = new SegmentedFlowEntry(flows.getChildren().size(), this, slot.execution.getInstruction(),
                    registerStart, slot.execution.getInstructionId(), cycle);
            entries.put(slot.execution.getInstructionId(), entry);
            flows.getChildren().add(entry);

            if (entries.size() > maxItems) {
                var toRemove = (SegmentedFlowEntry) flows.getChildren().remove(0);
                entries.remove(toRemove.getInstructionNumber());
            }
        }

        entry.addStep(cycle, step, stepSize, firstCycle, slot.status);
    }

    @Override
    public long getFirstCycle() {
        return firstCycle;
    }

    @Override
    public long getLastCycle() {
        if (flows.getChildren().isEmpty()) return 0;

        var last = (SegmentedFlowEntry) flows.getChildren().get(flows.getChildren().size() - 1);
        return last.getLastCycle();
    }

    public void refresh() {
        int index = 0;
        for (Node child : flows.getChildren()) {
            if (child instanceof SegmentedFlowEntry) {
                ((SegmentedFlowEntry) child).refresh(index++, stepSize, firstCycle);
            }
        }
    }

    @Listener(priority = Integer.MIN_VALUE)
    private void onInstructionExecuted(MultiALUPipelineShiftEvent.After event) {
        //Adding items to a separate list prevents the app to block.
        toAdd.add(event);
        pipelines.add(event.getPipeline().copy());

        if (toAdd.size() > maxItems << 2) {
            toAdd.removeFirst();
            pipelines.removeFirst();
        }
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        Platform.runLater(this::flushEvents);
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        flows.getChildren().clear();
        entries.clear();
        refreshVisualizer();
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent.After event) {
        var index = flows.getChildren().size() - 1;

        var iterator = flows.getChildren().iterator();
        while (iterator.hasNext()) {
            var node = iterator.next();
            if (node instanceof SegmentedFlowEntry entry && entry.removeCycle(event.getUndoCycle()) && entry.isEmpty()) {
                entries.remove(entry.getInstructionNumber());
                iterator.remove();
            }
        }
        refreshVisualizer();
    }
}
