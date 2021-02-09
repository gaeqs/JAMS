package net.jamsimulator.jams.gui.mips.simulator.instruction.type;

import javafx.application.Platform;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSCompiledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSCompiledLine;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class MIPSPipelinedCompiledCodeViewer extends MIPSCompiledCodeViewer {

    private int[] previousLines;

    public MIPSPipelinedCompiledCodeViewer(Simulation<?> simulation) {
        super(simulation);

        previousLines = new int[MultiCycleStep.values().length];

        var sim = (PipelinedSimulation) simulation;
        var pipeline = sim.getPipeline();

        for (MultiCycleStep step : MultiCycleStep.values()) {
            int pcVal = pipeline.getPc(step);

            var optional = compiledLines.stream()
                    .filter(target -> target.getAddress().filter(v -> v == pcVal).isPresent())
                    .findFirst();
            previousLines[step.ordinal()] = optional.map(MIPSCompiledLine::getLine).orElse(-1);

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
                    int address = compiledLines.get(previousLine).getAddress().orElse(-1);
                    boolean breakpoint = address != -1 && simulation.getBreakpoints().contains(address);
                    setParagraphStyle(previousLine, breakpoint ? Set.of("instruction-breakpoint") : Collections.emptyList());
                }
            }

            var sim = (PipelinedSimulation) simulation;
            var pipeline = sim.getPipeline();
            for (MultiCycleStep step : MultiCycleStep.values()) {
                int pcVal = pipeline.getPc(step);

                var optional = compiledLines.stream()
                        .filter(target -> target.getAddress().filter(v -> v == pcVal).isPresent())
                        .findFirst();
                previousLines[step.ordinal()] = optional.map(MIPSCompiledLine::getLine).orElse(-1);

                if (previousLines[step.ordinal()] != -1) {
                    setParagraphStyle(previousLines[step.ordinal()], Set.of(step.getStyle()));
                }
            }
        });
    }

    @Override
    protected boolean isLineBeingUsed(int line) {
        return line != -1 && Arrays.stream(previousLines).anyMatch(v -> v == line);
    }
}
