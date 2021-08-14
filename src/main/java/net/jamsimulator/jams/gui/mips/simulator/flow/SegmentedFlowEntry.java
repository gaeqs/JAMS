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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an instruction inside a {@link FlowTable} watching a multi-cycle simulation.
 */
public class SegmentedFlowEntry extends FlowEntry {

    public static int INSTRUCTION_SIZE = 200;

    private final Map<Long, Label> labels;
    private final long startingCycle, instructionNumber;

    public SegmentedFlowEntry(int index, FlowTable table, AssembledInstruction instruction, String registerStart, long instructionNumber, long startingCycle) {
        super(index, table, instruction, registerStart);
        this.labels = new HashMap<>();
        this.startingCycle = startingCycle;
        this.instructionNumber = instructionNumber;
    }

    /**
     * Returns the instruction number of this entry.
     *
     * @return the instruction number.
     */
    public long getInstructionNumber() {
        return instructionNumber;
    }

    /**
     * Returns the cycle of the first label inside this entry.
     *
     * @return the cycle.
     */
    public long getStartingCycle() {
        return startingCycle;
    }

    public long getLastCycle() {
        if (labels.isEmpty()) return startingCycle;
        return startingCycle + labels.size() - 1;
    }

    /**
     * Adds a step to this entry.
     *
     * @param cycle      the cycle of the step.
     * @param step       the step type.
     * @param stepSize   the size of the display.
     * @param firstCycle the first cycle the table is showing.
     * @param raw        whether the step should be tagged as raw.
     */
    public void addStep(long cycle, MultiCycleStep step, double stepSize, long firstCycle, boolean raw) {
        var label = new Label(raw ? "RAW" : step.getTag());
        label.getStyleClass().add(step.getStyle());
        label.setPrefWidth(stepSize);
        label.setAlignment(Pos.CENTER);

        var leftAnchor = INSTRUCTION_SIZE + (cycle - firstCycle) * stepSize;
        AnchorUtils.setAnchor(label, 0, 0, leftAnchor, -1);

        labels.put(cycle, label);
        getChildren().add(0, label);
    }

    /**
     * Refreshes the position and size of all labels inside this entry.
     *
     * @param index      the index of the entry inside the table.
     * @param stepSize   the size of all displayed labels.
     * @param firstCycle the first cycle the table is showing.
     */
    public void refresh(int index, double stepSize, long firstCycle) {
        refreshStyle(index);

        labels.forEach((cycle, label) -> {
            label.setPrefWidth(stepSize);
            var leftAnchor = INSTRUCTION_SIZE + (cycle - firstCycle) * stepSize;
            AnchorUtils.setAnchor(label, 0, 0, leftAnchor, -1);
        });
    }

    /**
     * Removes the label that matches the given cycle.
     *
     * @param cycle the cycle.
     * @return whether any label was removed.
     */
    public boolean removeCycle(long cycle) {
        Label label = labels.remove(cycle);
        if (label != null) {
            getChildren().remove(label);
            return true;
        }
        return false;
    }

    /**
     * Returns whether this entry has no labels.
     *
     * @return whether this entry has no labels.
     */
    public boolean isEmpty() {
        return labels.isEmpty();
    }
}
