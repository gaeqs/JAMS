package net.jamsimulator.jams.gui.mips.flow.multicycle;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.mips.flow.FlowEntry;
import net.jamsimulator.jams.gui.mips.flow.FlowTable;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.HashMap;
import java.util.Map;

public class MultiCycleFlowEntry extends FlowEntry {

	public static int INSTRUCTION_SIZE = 200;

	private final Map<Long, Label> labels;

	private final long startingCycle, instructionNumber;

	public MultiCycleFlowEntry(int index, ScrollPane scrollPane, FlowTable table, AssembledInstruction instruction, String registerStart, long instructionNumber, long startingCycle) {
		super(index, scrollPane, table, instruction, registerStart);
		labels = new HashMap<>();
		this.startingCycle = startingCycle;
		this.instructionNumber = instructionNumber;
	}

	public void refresh(int index, double stepSize, long firstCycle) {
		refresh(index);

		labels.forEach((cycle, label) -> {
			label.setPrefWidth(stepSize);
			AnchorUtils.setAnchor(label, 0, 0,
					INSTRUCTION_SIZE + (cycle - firstCycle) * stepSize, -1);
		});
	}

	public long getInstructionNumber() {
		return instructionNumber;
	}

	public long getStartingCycle() {
		return startingCycle;
	}

	public void addStep(long cycle, MultiCycleStep step, double stepSize, long firstCycle) {
		Label label = new Label(step.getTag());
		label.getStyleClass().add(step.getStyle());
		label.setPrefWidth(stepSize);
		label.setAlignment(Pos.CENTER);

		labels.put(cycle, label);
		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + (cycle - firstCycle) * stepSize, -1);

		getChildren().add(0, label);
	}

	public boolean removeCycle(long cycle) {
		Label label = labels.remove(cycle);
		if (label != null) {
			getChildren().remove(label);
		}
		return labels.isEmpty();
	}

}
