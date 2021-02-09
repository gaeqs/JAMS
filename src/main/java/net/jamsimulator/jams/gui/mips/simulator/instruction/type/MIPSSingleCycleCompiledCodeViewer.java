package net.jamsimulator.jams.gui.mips.simulator.instruction.type;

import javafx.application.Platform;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSCompiledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSCompiledLine;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Collections;
import java.util.Set;

public class MIPSSingleCycleCompiledCodeViewer extends MIPSCompiledCodeViewer {

    private int previousLine;

    public MIPSSingleCycleCompiledCodeViewer(Simulation<?> simulation) {
        super(simulation);

        var optional = compiledLines.stream()
                .filter(target -> target.getAddress().filter(v -> v == pc.getValue()).isPresent())
                .findFirst();

        previousLine = optional.map(MIPSCompiledLine::getLine).orElse(-1);

        if (previousLine != -1) {
            setParagraphStyle(previousLine, Set.of("instruction-execute"));
        }
    }

    @Override
    protected void refresh() {
        Platform.runLater(() -> {
            if (previousLine != -1) {
                int address = compiledLines.get(previousLine).getAddress().orElse(-1);
                boolean breakpoint = address != -1 && simulation.getBreakpoints().contains(address);
                setParagraphStyle(previousLine, breakpoint ? Set.of("instruction-breakpoint") : Collections.emptyList());
            }

            var optional = compiledLines.stream()
                    .filter(target -> target.getAddress().filter(v -> v == pc.getValue()).isPresent())
                    .findFirst();

            previousLine = optional.map(MIPSCompiledLine::getLine).orElse(-1);
            if (previousLine != -1) {
                setParagraphStyle(previousLine, Set.of("instruction-execute"));
            }
        });
    }

    @Override
    protected boolean isLineBeingUsed(int line) {
        return previousLine != -1 && previousLine == line;
    }
}
