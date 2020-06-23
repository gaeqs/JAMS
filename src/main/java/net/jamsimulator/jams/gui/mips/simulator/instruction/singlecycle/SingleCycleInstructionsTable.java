package net.jamsimulator.jams.gui.mips.simulator.instruction.singlecycle;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionEntry;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionsTable;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Map;

public class SingleCycleInstructionsTable extends InstructionsTable {

	public static final String CURRENT_INSTRUCTION_STYLE_CLASS = "single-cycle-current-instruction";

	private final Register pc;

	public SingleCycleInstructionsTable(Simulation<? extends SingleCycleArchitecture> simulation, Map<Integer, String> originals) {
		super(simulation, originals);
		simulation.getRegisterSet().registerListeners(this, true);
		pc = simulation.getRegisterSet().getProgramCounter();
	}

	@Listener
	private void onInstructionExecution(RegisterChangeValueEvent.After event) {
		if (event.getRegister().equals(pc)) {
			refresh();
		}
	}

	@Override
	public void onRowUpdate(TableRow<InstructionEntry> row) {
		int current = pc.getValue();
		if (row.getItem() != null && row.getItem().getAddress() == current) {
			Platform.runLater(() -> {
				for (Node child : row.getChildrenUnmodifiable()) {
					if (!child.getStyleClass().contains(CURRENT_INSTRUCTION_STYLE_CLASS)) {
						child.getStyleClass().add(CURRENT_INSTRUCTION_STYLE_CLASS);
					}
				}
			});
		} else {
			for (Node child : row.getChildrenUnmodifiable()) {
				child.getStyleClass().remove(CURRENT_INSTRUCTION_STYLE_CLASS);
			}
		}
	}
}
