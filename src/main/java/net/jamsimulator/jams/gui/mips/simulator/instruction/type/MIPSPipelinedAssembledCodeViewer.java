package net.jamsimulator.jams.gui.mips.simulator.instruction.type;

import javafx.application.Platform;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledLine;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class MIPSPipelinedAssembledCodeViewer extends MIPSAssembledCodeViewer {

    private final int[] previousLines;

    public MIPSPipelinedAssembledCodeViewer(Simulation<?> simulation, boolean kernel) {
        super(simulation, kernel);

        previousLines = new int[MultiCycleStep.values().length];

        var sim = (PipelinedSimulation) simulation;
        var pipeline = sim.getPipeline();

        for (MultiCycleStep step : MultiCycleStep.values()) {
            int pcVal = pipeline.getPc(step);

            var optional = assembledLines.stream()
                    .filter(target -> target.getAddress().filter(v -> v == pcVal).isPresent())
                    .findFirst();
            previousLines[step.ordinal()] = optional.map(MIPSAssembledLine::getLine).orElse(-1);

            if (previousLines[step.ordinal()] != -1) {
                setParagraphStyle(previousLines[step.ordinal()], Set.of(step.getStyle()));
            }
        }
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

            var sim = (PipelinedSimulation) simulation;
            var pipeline = sim.getPipeline();
            for (MultiCycleStep step : MultiCycleStep.values()) {
                int pcVal = pipeline.getPc(step);

                var optional = assembledLines.stream()
                        .filter(target -> target.getAddress().filter(v -> v == pcVal).isPresent())
                        .findFirst();
                previousLines[step.ordinal()] = optional.map(MIPSAssembledLine::getLine).orElse(-1);

                if (previousLines[step.ordinal()] != -1) {
                    setParagraphStyle(previousLines[step.ordinal()], Set.of(step.getStyle()));
                }
            }
        });
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
        return line != -1 && Arrays.stream(previousLines).anyMatch(v -> v == line);
    }

    @Listener
    private void onPipelineShift(PipelineShiftEvent.After event) {
        boolean newFullSpeed = simulation.getCycleDelay() == 0;
        if (!fullSpeed && newFullSpeed) {
            clearStyles();
        }
        if (shouldUpdate || !newFullSpeed) {
            refresh();
        }

        fullSpeed = newFullSpeed;
    }
}
