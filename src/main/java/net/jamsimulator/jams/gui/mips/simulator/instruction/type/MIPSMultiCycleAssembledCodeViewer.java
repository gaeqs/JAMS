package net.jamsimulator.jams.gui.mips.simulator.instruction.type;

import javafx.application.Platform;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledLine;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.multicycle.event.MultiCycleStepEvent;

import java.util.Collections;
import java.util.Set;

public class MIPSMultiCycleAssembledCodeViewer extends MIPSAssembledCodeViewer {

    private int previousLine;

    public MIPSMultiCycleAssembledCodeViewer(Simulation<?> simulation, boolean kernel) {
        super(simulation, kernel);

        var sim = (MultiCycleSimulation) simulation;
        int pcVal = sim.getCurrentStep() != MultiCycleStep.FETCH ? pc.getValue() - 4 : pc.getValue();
        var optional = assembledLines.stream()
                .filter(target -> target.getAddress().filter(v -> v == pcVal).isPresent())
                .findFirst();

        previousLine = optional.map(MIPSAssembledLine::getLine).orElse(-1);

        if (previousLine != -1) {
            setParagraphStyle(previousLine, Set.of(sim.getCurrentStep().getStyle()));
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

            var sim = (MultiCycleSimulation) simulation;
            int pcVal = sim.getCurrentStep() != MultiCycleStep.FETCH ? pc.getValue() - 4 : pc.getValue();
            var optional = assembledLines.stream()
                    .filter(target -> target.getAddress().filter(v -> v == pcVal).isPresent())
                    .findFirst();

            previousLine = optional.map(MIPSAssembledLine::getLine).orElse(-1);

            if (previousLine != -1) {
                setParagraphStyle(previousLine, Set.of(sim.getCurrentStep().getStyle()));
            }
        });
    }

    @Override
    protected void clearStyles() {
        Platform.runLater(() -> {
            if (previousLine != -1) {
                int address = assembledLines.get(previousLine).getAddress().orElse(-1);
                boolean breakpoint = address != -1 && simulation.hasBreakpoint(address);
                setParagraphStyle(previousLine, breakpoint ? Set.of("instruction-breakpoint") : Collections.emptyList());
            }
        });
    }

    @Override
    protected boolean isLineBeingUsed(int line) {
        return previousLine != -1 && line == previousLine;
    }

    @Listener
    private void onMultiCycleStep(MultiCycleStepEvent.After event) {
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
