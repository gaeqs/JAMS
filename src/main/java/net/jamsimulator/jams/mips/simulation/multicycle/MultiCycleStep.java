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

package net.jamsimulator.jams.mips.simulation.multicycle;

import javafx.scene.Node;

public enum MultiCycleStep {

    FETCH("F", "instruction-fetch", null),
    DECODE("D", "instruction-decode", FETCH),
    EXECUTE("E", "instruction-execute", DECODE),
    MEMORY("M", "instruction-memory", EXECUTE),
    WRITE_BACK("W", "instruction-write-back", MEMORY);

    private final String tag;
    private final String style;
    private final MultiCycleStep previous;

    MultiCycleStep(String tag, String style, MultiCycleStep previous) {
        this.tag = tag;
        this.style = style;
        this.previous = previous;
    }

    public static void removeAllStyles(Node node) {
        for (MultiCycleStep value : values()) {
            node.getStyleClass().remove(value.style);
        }
    }

    public String getTag() {
        return tag;
    }

    public String getStyle() {
        return style;
    }

    public MultiCycleStep getPreviousStep() {
        return previous == null ? FETCH : previous;
    }
}
