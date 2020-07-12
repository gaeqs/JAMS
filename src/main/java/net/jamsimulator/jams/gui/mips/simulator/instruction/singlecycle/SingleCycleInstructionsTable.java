package net.jamsimulator.jams.gui.mips.simulator.instruction.singlecycle;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionEntry;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionsTable;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationFinishedEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.Map;

public class SingleCycleInstructionsTable extends InstructionsTable {

	public static final String CURRENT_INSTRUCTION_STYLE_CLASS = "single-cycle-current-instruction";
	public static final String NEXT_INSTRUCTION_STYLE_CLASS = "single-cycle-next-instruction";

	private final Register pc;

	public SingleCycleInstructionsTable(Simulation<? extends SingleCycleArchitecture> simulation, Map<Integer, String> originals) {
		super(simulation, originals);
		simulation.registerListeners(this, true);
		simulation.getRegisters().registerListeners(this, true);
		pc = simulation.getRegisters().getProgramCounter();
	}

	@Listener
	private void onInstructionExecution(RegisterChangeValueEvent.After event) {
		if (event.getRegister().equals(pc)) {
			refresh();
		}
	}

	@Listener
	private void onSimulationStart(SimulationStartEvent event) {
		refresh();
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		refresh();
	}

	@Listener
	private void onSimulationFinishes(SimulationFinishedEvent event) {
		refresh();
	}

	@Listener
	private void onMemoryEndianness(MemoryEndiannessChange.After event) {
		refresh();
	}


	@Override
	public void onRowUpdate(TableRow<InstructionEntry> row) {
		int current = pc.getValue();
		String style = simulation.isRunning() ? CURRENT_INSTRUCTION_STYLE_CLASS : NEXT_INSTRUCTION_STYLE_CLASS;
		if (simulation.isRunning()) current -= 4;

		if (!simulation.isFinished() && row.getItem() != null && row.getItem().getAddress() == current) {
			Platform.runLater(() -> {
				for (Node child : row.getChildrenUnmodifiable()) {
					child.getStyleClass().remove(CURRENT_INSTRUCTION_STYLE_CLASS);
					child.getStyleClass().remove(NEXT_INSTRUCTION_STYLE_CLASS);
					if (!child.getStyleClass().contains(style)) {
						child.getStyleClass().add(style);
					}
				}
			});
		} else {
			for (Node child : row.getChildrenUnmodifiable()) {
				child.getStyleClass().remove(CURRENT_INSTRUCTION_STYLE_CLASS);
				child.getStyleClass().remove(NEXT_INSTRUCTION_STYLE_CLASS);
			}
		}
	}
}
