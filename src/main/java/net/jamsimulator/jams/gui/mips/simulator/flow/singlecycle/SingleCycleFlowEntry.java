package net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowEntry;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.utils.AnchorUtils;

public class SingleCycleFlowEntry extends FlowEntry {

	private final Label label;

	public SingleCycleFlowEntry(int index, ScrollPane scrollPane, FlowTable table, AssembledInstruction instruction, String registerStart, double stepSize) {
		super(index, scrollPane, table, instruction, registerStart);
		label = new Label("E");
		label.getStyleClass().add("single-cycle-execute");
		label.setPrefWidth(stepSize);
		label.setAlignment(Pos.CENTER);

		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + index * stepSize, -1);
		getChildren().add(0, label);
	}

	public void refresh(int index, double stepSize) {
		refresh(index);
		label.setPrefWidth(stepSize);
		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + index * stepSize, -1);
	}

}
