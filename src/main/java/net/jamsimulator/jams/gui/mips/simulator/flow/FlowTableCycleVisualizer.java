package net.jamsimulator.jams.gui.mips.simulator.flow;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.gui.util.AnchorUtils;

public class FlowTableCycleVisualizer extends AnchorPane {

	private final FlowTable flowTable;
	private final LanguageLabel cycleLabel;

	public FlowTableCycleVisualizer(FlowTable flowTable, ScrollPane anchorScrollPane) {
		getStyleClass().add("anchor-pane");
		this.flowTable = flowTable;
		this.cycleLabel = new LanguageLabel(Messages.FLOW_CYCLE);
		cycleLabel.getStyleClass().add("flow-current-cycle-label");
		cycleLabel.setPrefWidth(FlowEntry.INSTRUCTION_SIZE);

		anchorScrollPane.viewportBoundsProperty().addListener((obs, old, val) -> {
			AnchorUtils.setAnchor(this,
					-anchorScrollPane.getViewportBounds().getMinY() /  flowTable.scalableNode.scaleY(),
					-1, 0, 0);
			AnchorUtils.setAnchor(cycleLabel, 0, 0,
					-anchorScrollPane.getViewportBounds().getMinX() /  flowTable.scalableNode.scaleX(),
					-1);
		});
		anchorScrollPane.vvalueProperty().addListener((obs, old, val) ->
				AnchorUtils.setAnchor(this,
						-anchorScrollPane.getViewportBounds().getMinY() /  flowTable.scalableNode.scaleY(),
						-1, 0, 0));

		anchorScrollPane.hvalueProperty().addListener((obs, old, val) ->
				AnchorUtils.setAnchor(cycleLabel, 0, 0,
						-anchorScrollPane.getViewportBounds().getMinX() / flowTable.scalableNode.scaleX(),
						-1));
	}

	public FlowTable getFlowTable() {
		return flowTable;
	}

	public void refresh() {
		getChildren().clear();
		long first = flowTable.getFirstCycle();
		long last = flowTable.getLastCycle();
		long difference = last - first;

		if(flowTable.flows.getChildren().isEmpty()) return;

		for (long i = 0; i <= difference; i++) {
			var label = new Label("+" + i);
			label.setPrefWidth(flowTable.stepSize);
			AnchorUtils.setAnchor(label, 0, 0, FlowEntry.INSTRUCTION_SIZE + flowTable.stepSize * i, 0);

			getChildren().add(label);
		}


		cycleLabel.setReplacements(new String[] {"{CYCLE}", String.valueOf(first)});
		getChildren().add(cycleLabel);
		cycleLabel.toFront();
	}

	public void setStepSize(double stepSize) {

		var size = getChildren().size() -1 ;
		Label label;
		for(int i = 0; i < size; i++) {
			label = (Label) getChildren().get(i);
			label.setPrefWidth(stepSize);
			AnchorUtils.setAnchor(label, 0, 0, FlowEntry.INSTRUCTION_SIZE + flowTable.stepSize * i, 0);
		}
	}

}
