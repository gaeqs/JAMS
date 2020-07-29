package net.jamsimulator.jams.gui.mips.flow.singlecycle;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.utils.AnchorUtils;

public class SingleCycleFlowEntry extends AnchorPane {

	public static int INSTRUCTION_SIZE = 200;

	private final Label label;

	public SingleCycleFlowEntry(int index, AssembledInstruction instruction, ScrollPane scrollPane, String registerStart, double stepSize) {
		setPrefWidth(20);
		getStyleClass().add("flow-entry");
		getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));

		prefWidthProperty().bind(scrollPane.widthProperty());

		Label instructionLabel = new Label(instruction.getBasicOrigin().getMnemonic() + " " + instruction.parametersToString(registerStart));
		instructionLabel.setPrefWidth(INSTRUCTION_SIZE);
		instructionLabel.getStyleClass().add("flow-entry-instruction-label");

		label = new Label("E");
		label.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FF0000"), null, null)));
		label.setPrefWidth(stepSize);


		if (scrollPane != null) {
			scrollPane.viewportBoundsProperty().addListener((obs, old, val) -> AnchorUtils.setAnchor(instructionLabel, 0, 0, -scrollPane.getViewportBounds().getMinX(), -1));
			scrollPane.hvalueProperty().addListener((obs, old, val) -> AnchorUtils.setAnchor(instructionLabel, 0, 0, -scrollPane.getViewportBounds().getMinX(), -1));
			AnchorUtils.setAnchor(instructionLabel, 0, 0, -scrollPane.getViewportBounds().getMinX(), -1);
		} else {
			AnchorUtils.setAnchor(instructionLabel, 0, 0, 0, -1);
		}

		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + index * stepSize, -1);

		getChildren().addAll(label, instructionLabel);
	}

	public void setIndex(int index, double stepSize) {
		getStyleClass().remove("flow-entry-odd");
		getStyleClass().remove("flow-entry-even");
		getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));

		label.setPrefWidth(stepSize);
		AnchorUtils.setAnchor(label, 0, 0, INSTRUCTION_SIZE + index * stepSize, -1);
	}

}
