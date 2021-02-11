package net.jamsimulator.jams.gui.mips.simulator.flow;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.gui.util.AnchorUtils;

/**
 * Represents an instruction inside a {@link FlowTable}.
 * <p>
 * Different {@link FlowTable} implementations should extends this class to provide their characteristics.
 */
public class FlowEntry extends AnchorPane {

	public static int INSTRUCTION_SIZE = 200;

	private final FlowTable table;

	public FlowEntry(int index, FlowTable table, AssembledInstruction instruction, String registerStart) {
		this.table = table;

		setPrefWidth(20);
		getStyleClass().add("flow-entry");
		getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));

		var instructionLabel = new Label(instruction.getBasicOrigin().getMnemonic() + " " + instruction.parametersToString(registerStart));
		instructionLabel.setPrefWidth(INSTRUCTION_SIZE);
		instructionLabel.getStyleClass().add("flow-entry-instruction-label");
		getChildren().add(instructionLabel);

		initListeners(instructionLabel);
	}

	public void refreshStyle(int index) {
		getStyleClass().remove("flow-entry-odd");
		getStyleClass().remove("flow-entry-even");
		getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));
	}

	private void initListeners(Label instructionLabel) {
		if (table.scrollPane != null) {
			prefWidthProperty().bind(table.scrollPane.widthProperty());

			table.scrollPane.viewportBoundsProperty().addListener((obs, old, val) ->
					AnchorUtils.setAnchor(instructionLabel, 0, 0, -table.scrollPane.getViewportBounds().getMinX() / table.getScaleX(), -1));
			table.scrollPane.hvalueProperty().addListener((obs, old, val) ->
					AnchorUtils.setAnchor(instructionLabel, 0, 0, -table.scrollPane.getViewportBounds().getMinX() / table.getScaleX(), -1));

			AnchorUtils.setAnchor(instructionLabel, 0, 0, -table.scrollPane.getViewportBounds().getMinX() / table.getScaleX(), -1);
		} else {
			AnchorUtils.setAnchor(instructionLabel, 0, 0, 0, -1);
		}
		setOnMouseClicked(event -> table.select(this));
	}
}
