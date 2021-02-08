package net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowEntry;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.utils.AnchorUtils;

public class SingleCycleFlowEntry extends FlowEntry {

	private final Label label;
	private final long cycle;

	public SingleCycleFlowEntry(int index,
								FlowTable table,
								AssembledInstruction instruction,
								String registerStart,
								long cycle,
								double stepSize) {
		super(index, table, instruction, registerStart);
		this.cycle = cycle;
		label = new Label("E");
		label.getStyleClass().add("single-cycle-execute");
		label.setPrefWidth(stepSize);
		label.setAlignment(Pos.CENTER);

		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + index * stepSize, -1);
		getChildren().add(0, label);
	}

	public void refresh(int index, double stepSize) {
		refreshStyle(index);
		label.setPrefWidth(stepSize);
		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + index * stepSize, -1);
	}

	public long getCycle() {
		return cycle;
	}

}
