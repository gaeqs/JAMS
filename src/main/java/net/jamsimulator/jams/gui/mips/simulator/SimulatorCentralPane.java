package net.jamsimulator.jams.gui.mips.simulator;

import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.mips.simulator.execution.ExecutionButtons;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionsTable;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.Map;

public class SimulatorCentralPane extends AnchorPane {


	public SimulatorCentralPane(Simulation<?> simulation, Map<Integer, String> originals) {
		ExecutionButtons buttons = new ExecutionButtons();
		AnchorUtils.setAnchor(buttons, 0, -1, 0, 0);
		buttons.setPrefHeight(30);

		InstructionsTable table = new InstructionsTable(simulation, originals);
		AnchorUtils.setAnchor(table, 29, 0, 0, 0);

		getChildren().addAll(buttons, table);
	}

}
