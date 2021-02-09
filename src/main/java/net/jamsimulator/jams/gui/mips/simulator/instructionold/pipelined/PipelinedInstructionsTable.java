package net.jamsimulator.jams.gui.mips.simulator.instructionold.pipelined;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instructionold.InstructionEntry;
import net.jamsimulator.jams.gui.mips.simulator.instructionold.InstructionsTable;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.*;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.Map;

public class PipelinedInstructionsTable extends InstructionsTable {

	public PipelinedInstructionsTable(Simulation<? extends PipelinedArchitecture> simulation, Map<Integer, String> originals, boolean kernel) {
		super(simulation, originals, kernel);
		if (!(simulation instanceof PipelinedSimulation))
			throw new IllegalArgumentException("Simulation must be a PipelinedSimulation.");
		simulation.registerListeners(this, true);
		if (!simulation.isRunning()) {
			simulation.getRegisters().registerListeners(this, true);
		}
	}

	@Listener
	private void onPipelineShift(PipelineShiftEvent.After event) {
		refresh();
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
	private void onSimulationReset(SimulationResetEvent event) {
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
		if (simulation.isFinished() || row.getItem() == null) {
			Platform.runLater(() -> {
				for (Node child : row.getChildrenUnmodifiable()) {
					MultiCycleStep.removeAllStyles(child);
				}
			});
			return;
		}


		var pipeline = ((PipelinedSimulation) simulation).getPipeline();
		var step = pipeline.getStepOf(row.getItem().getAddress());

		if (step != null) {
			Platform.runLater(() -> {
				for (Node child : row.getChildrenUnmodifiable()) {
					MultiCycleStep.removeAllStyles(child);
					if (!child.getStyleClass().contains(step.getStyle())) {
						child.getStyleClass().add(step.getStyle());
					}
				}
			});
		} else {
			Platform.runLater(() -> {
				for (Node child : row.getChildrenUnmodifiable()) {
					MultiCycleStep.removeAllStyles(child);
				}
			});
		}
	}
}
