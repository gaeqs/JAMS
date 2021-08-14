/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.simulator.flow;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

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
                    -anchorScrollPane.getViewportBounds().getMinY() / flowTable.scalableNode.scaleY(),
                    -1, 0, 0);
            AnchorUtils.setAnchor(cycleLabel, 0, 0,
                    -anchorScrollPane.getViewportBounds().getMinX() / flowTable.scalableNode.scaleX(),
                    -1);
        });
        anchorScrollPane.vvalueProperty().addListener((obs, old, val) ->
                AnchorUtils.setAnchor(this,
                        -anchorScrollPane.getViewportBounds().getMinY() / flowTable.scalableNode.scaleY(),
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

        if (flowTable.flows.getChildren().isEmpty()) return;

        for (long i = 0; i <= difference; i++) {
            var label = new Label("+" + i);
            label.setPrefWidth(flowTable.stepSize);
            AnchorUtils.setAnchor(label, 0, 0, FlowEntry.INSTRUCTION_SIZE + flowTable.stepSize * i, 0);

            getChildren().add(label);
        }


        cycleLabel.setReplacements(new String[]{"{CYCLE}", String.valueOf(first)});
        getChildren().add(cycleLabel);
        cycleLabel.toFront();
    }

    public void setStepSize(double stepSize) {

        var size = getChildren().size() - 1;
        Label label;
        for (int i = 0; i < size; i++) {
            label = (Label) getChildren().get(i);
            label.setPrefWidth(stepSize);
            AnchorUtils.setAnchor(label, 0, 0, FlowEntry.INSTRUCTION_SIZE + flowTable.stepSize * i, 0);
        }
    }

}
