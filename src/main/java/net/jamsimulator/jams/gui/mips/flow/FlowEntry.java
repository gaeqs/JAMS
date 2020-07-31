package net.jamsimulator.jams.gui.mips.flow;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents an entry inside a {@link FlowTable}.
 * <p>
 * Different {@link FlowTable} implementations should extends this class to provide their characteristics.
 */
public class FlowEntry extends AnchorPane {

	public static int INSTRUCTION_SIZE = 200;

	public FlowEntry(int index, ScrollPane scrollPane, FlowTable table, AssembledInstruction instruction, String registerStart) {
		setPrefWidth(20);
		getStyleClass().add("flow-entry");
		getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));

		Label instructionLabel = new Label(instruction.getBasicOrigin().getMnemonic() + " " + instruction.parametersToString(registerStart));
		instructionLabel.setPrefWidth(INSTRUCTION_SIZE);
		instructionLabel.getStyleClass().add("flow-entry-instruction-label");

		if (scrollPane != null) {
			prefWidthProperty().bind(scrollPane.widthProperty());
			scrollPane.viewportBoundsProperty().addListener((obs, old, val) -> AnchorUtils.setAnchor(instructionLabel, 0, 0, -scrollPane.getViewportBounds().getMinX() / table.getScaleX(), -1));
			scrollPane.hvalueProperty().addListener((obs, old, val) -> AnchorUtils.setAnchor(instructionLabel, 0, 0, -scrollPane.getViewportBounds().getMinX() / table.getScaleX(), -1));
			AnchorUtils.setAnchor(instructionLabel, 0, 0, -scrollPane.getViewportBounds().getMinX() / table.getScaleX(), -1);
		} else {
			AnchorUtils.setAnchor(instructionLabel, 0, 0, 0, -1);
		}

		setOnMouseClicked(event -> table.select(this));

		getChildren().add(instructionLabel);
	}

	public void refresh(int index) {
		getStyleClass().remove("flow-entry-odd");
		getStyleClass().remove("flow-entry-even");
		getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));
	}
}
