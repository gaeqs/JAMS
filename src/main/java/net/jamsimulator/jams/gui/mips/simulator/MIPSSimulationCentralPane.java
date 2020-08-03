package net.jamsimulator.jams.gui.mips.simulator;

import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.mips.simulator.execution.ExecutionButtons;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionsTable;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.Map;

public class MIPSSimulationCentralPane extends AnchorPane implements ActionRegion {


	public MIPSSimulationCentralPane(Simulation<?> simulation) {
		ExecutionButtons buttons = new ExecutionButtons(simulation);
		AnchorUtils.setAnchor(buttons, 0, -1, 0, 0);
		buttons.setPrefHeight(30);

		InstructionsTable table = InstructionsTable.createTable(simulation.getArchitecture(), simulation, simulation.getData().getOriginalInstructions());
		AnchorUtils.setAnchor(table, 29, 0, 0, 0);

		getChildren().addAll(buttons, table);
	}

	@Override
	public boolean supportsActionRegion(String region) {
		return RegionTags.MIPS_SIMULATION.equals(region);
	}

}
