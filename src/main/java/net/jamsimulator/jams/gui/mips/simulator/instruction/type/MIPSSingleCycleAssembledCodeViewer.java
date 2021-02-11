package net.jamsimulator.jams.gui.mips.simulator.instruction.type;

import javafx.application.Platform;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledLine;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Collections;
import java.util.Set;

public class MIPSSingleCycleAssembledCodeViewer extends MIPSAssembledCodeViewer {

    private int previousLine;

    public MIPSSingleCycleAssembledCodeViewer(Simulation<?> simulation, boolean kernel) {
        super(simulation, kernel);

        var optional = assembledLines.stream()
                .filter(target -> target.getAddress().filter(v -> v == pc.getValue()).isPresent())
                .findFirst();

        previousLine = optional.map(MIPSAssembledLine::getLine).orElse(-1);

        if (previousLine != -1) {
            setParagraphStyle(previousLine, Set.of("instruction-execute"));
        }
    }

    @Override
    protected void refresh() {
        Platform.runLater(() -> {
            if (previousLine != -1) {
                int address = assembledLines.get(previousLine).getAddress().orElse(-1);
                boolean breakpoint = address != -1 && simulation.hasBreakpoint(address);
                setParagraphStyle(previousLine, breakpoint ? Set.of("instruction-breakpoint") : Collections.emptyList());
            }

            var optional = assembledLines.stream()
                    .filter(target -> target.getAddress().filter(v -> v == pc.getValue()).isPresent())
                    .findFirst();

            previousLine = optional.map(MIPSAssembledLine::getLine).orElse(-1);
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
