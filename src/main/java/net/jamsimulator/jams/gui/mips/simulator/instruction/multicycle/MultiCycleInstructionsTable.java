package net.jamsimulator.jams.gui.mips.simulator.instruction.multicycle;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionEntry;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionsTable;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.*;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;

import java.util.Map;

public class MultiCycleInstructionsTable extends InstructionsTable {

	private final Register pc;

	public MultiCycleInstructionsTable(Simulation<? extends MultiCycleArchitecture> simulation, Map<Integer, String> originals) {
		super(simulation, originals);
		if (!(simulation instanceof MultiCycleSimulation))
			throw new IllegalArgumentException("Simulation must be a MultiCycleSimulation.");
		simulation.registerListeners(this, true);
		pc = simulation.getRegisters().getProgramCounter();
		if (!simulation.isRunning()) {
			simulation.getRegisters().registerListeners(this, true);
		}
	}

	@Listener
	private void onInstructionExecution(RegisterChangeValueEvent.After event) {
		if (event.getRegister().equals(pc)) {
			refresh();
		}
	}

	@Listener
	private void onSimulationLock(SimulationLockEvent event) {
		simulation.getRegisters().registerListeners(this, true);
		refresh();
	}

	@Listener
	private void onSimulationUnlock(SimulationUnlockEvent event) {
		simulation.getRegisters().unregisterListeners(this);
	}

	@Listener
	private void onSimulationStart(SimulationStartEvent event) {
		simulation.getRegisters().unregisterListeners(this);
		refresh();
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		simulation.getRegisters().registerListeners(this, true);
		refresh();
	}

	@Listener
	private void onMemoryEndianness(MemoryEndiannessChange.After event) {
		refresh();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		refresh();
	}


	@Override
	public void onRowUpdate(TableRow<InstructionEntry> row) {
		int current = pc.getValue();

		String style = ((MultiCycleSimulation) simulation).getCurrentStep().getStyle();

		if (simulation.isRunning() || ((MultiCycleSimulation) simulation).getCurrentStep() != MultiCycleStep.FETCH)
			current -= 4;

		if (!simulation.isFinished() && row.getItem() != null && row.getItem().getAddress() == current) {
			Platform.runLater(() -> {
				for (Node child : row.getChildrenUnmodifiable()) {
					MultiCycleStep.removeAllStyles(child);
					if (!child.getStyleClass().contains(style)) {
						child.getStyleClass().add(style);
					}
				}
			});
		} else {
			for (Node child : row.getChildrenUnmodifiable()) {
				MultiCycleStep.removeAllStyles(child);
			}
		}
	}
}
