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

package net.jamsimulator.jams.gui.mips.simulator.instruction.type;

import javafx.application.Platform;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledLine;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.multiapupipelined.MultiAPUPipeline;
import net.jamsimulator.jams.mips.simulation.multiapupipelined.MultiAPUPipelineSlot;
import net.jamsimulator.jams.mips.simulation.multiapupipelined.MultiAPUPipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MIPSMultiAPUPipelinedAssembledCodeViewer extends MIPSAssembledCodeViewer {

    private final Set<Integer> previousLines = new HashSet<>();

    public MIPSMultiAPUPipelinedAssembledCodeViewer(MIPSSimulation<?> simulation, boolean kernel) {
        super(simulation, kernel);
        refreshLines();
    }

    @Override
    protected void refresh() {
        Platform.runLater(() -> {

            for (int previousLine : previousLines) {
                if (previousLine != -1) {
                    int address = assembledLines.get(previousLine).getAddress().orElse(-1);
                    boolean breakpoint = address != -1 && simulation.hasBreakpoint(address);
                    setParagraphStyle(previousLine, breakpoint ? Set.of("instruction-breakpoint") : Collections.emptyList());
                }
            }

            refreshLines();
        });
    }

    private void refreshLines() {
        var sim = (MultiAPUPipelinedSimulation) simulation;
        var pipeline = sim.getPipeline();

        for (MultiCycleStep step : MultiCycleStep.values()) {
            // Ignore fetch step.
            if (step == MultiCycleStep.FETCH) continue;
            var slots = getSlots(pipeline, step).stream().map(it -> it.pc).collect(Collectors.toSet());
            var lines = assembledLines.stream()
                    .filter(it -> it.getAddress().filter(slots::contains).isPresent())
                    .map(MIPSAssembledLine::getLine)
                    .collect(Collectors.toSet());
            previousLines.addAll(lines);

            var styles = Set.of(step.getStyle());
            lines.forEach(it -> setParagraphStyle(it, styles));
        }

        // Execute fetch.
        sim.runSynchronized(() -> {
            var pc = sim.getRegisters().getProgramCounter().getValue();
            var lines = assembledLines.stream()
                    .filter(it -> it.getAddress().filter(v -> v == pc).isPresent())
                    .map(MIPSAssembledLine::getLine)
                    .collect(Collectors.toSet());
            previousLines.addAll(lines);
            var styles = Set.of(MultiCycleStep.FETCH.getStyle());
            lines.forEach(it -> setParagraphStyle(it, styles));
        });

    }

    private Set<MultiAPUPipelineSlot> getSlots(MultiAPUPipeline pipeline, MultiCycleStep step) {
        return switch (step) {
            case FETCH -> pipeline.getFetch() == null ? Collections.emptySet() : Set.of(pipeline.getFetch());
            case DECODE -> pipeline.getDecode() == null ? Collections.emptySet() : Set.of(pipeline.getDecode());
            case EXECUTE -> pipeline.getExecute();
            case MEMORY -> pipeline.getMemory() == null ? Collections.emptySet() : Set.of(pipeline.getMemory());
            case WRITE_BACK -> pipeline.getWriteback() == null ? Collections.emptySet() : Set.of(pipeline.getWriteback());
        };
    }

    @Override
    protected void clearStyles() {
        Platform.runLater(() -> {
            for (int previousLine : previousLines) {
                if (previousLine != -1) {
                    int address = assembledLines.get(previousLine).getAddress().orElse(-1);
                    boolean breakpoint = address != -1 && simulation.hasBreakpoint(address);
                    setParagraphStyle(previousLine, breakpoint ? Set.of("instruction-breakpoint") : Collections.emptyList());
                }
            }
        });
    }

    @Override
    protected boolean isLineBeingUsed(int line) {
        return line != -1 && previousLines.contains(line);
    }

    @Listener
    private void onPipelineShift(PipelineShiftEvent.After event) {
        boolean newFullSpeed = simulation.getCycleDelay() == 0;
        if (!fullSpeed && newFullSpeed) {
            clearStyles();
        }
        if (shouldUpdate || !newFullSpeed) {
            refresh();
            refreshInstructions();
        }

        fullSpeed = newFullSpeed;
    }

}
