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
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;

/**
 * Represents an instruction inside a {@link FlowTable}.
 * <p>
 * Different {@link FlowTable} implementations should extend this class to provide their characteristics.
 */
public class FlowEntry extends AnchorPane {

    public static int INSTRUCTION_SIZE = 200;

    private final FlowTable table;

    public FlowEntry(int index, FlowTable table, AssembledInstruction instruction, String registerStart) {
        this.table = table;

        setPrefWidth(20);
        getStyleClass().add("flow-entry");
        getStyleClass().add("flow-entry-" + ((index & 1) == 0 ? "odd" : "even"));

        Label instructionLabel;
        if (instruction != null) {
            instructionLabel = new Label(instruction.getBasicOrigin().getMnemonic() + " " + instruction.parametersToString(registerStart));
        } else {
            instructionLabel = new Label("nop");
        }

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
                    AnchorUtils.setAnchor(instructionLabel, 0, 0, -table.scrollPane.getViewportBounds().getMinX() / table.scalableNode.scaleX(), -1));
            table.scrollPane.hvalueProperty().addListener((obs, old, val) ->
                    AnchorUtils.setAnchor(instructionLabel, 0, 0, -table.scrollPane.getViewportBounds().getMinX() / table.scalableNode.scaleX(), -1));

            AnchorUtils.setAnchor(instructionLabel, 0, 0, -table.scrollPane.getViewportBounds().getMinX() / table.scalableNode.scaleX(), -1);
        } else {
            AnchorUtils.setAnchor(instructionLabel, 0, 0, 0, -1);
        }
        setOnMouseClicked(event -> table.select(this));
    }
}
