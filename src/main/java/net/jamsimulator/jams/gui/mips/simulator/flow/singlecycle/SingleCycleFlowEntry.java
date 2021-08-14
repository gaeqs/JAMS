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

package net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowEntry;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;

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
        label.getStyleClass().add("instruction-execute");
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
