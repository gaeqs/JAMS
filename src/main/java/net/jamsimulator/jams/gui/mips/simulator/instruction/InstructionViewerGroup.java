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

package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class InstructionViewerGroup {

    private final MIPSAssembledCodeViewer user, kernel;
    private final Tab userTab, kernelTab;
    private final TabPane pane;

    public InstructionViewerGroup(MIPSAssembledCodeViewer user) {
        this.user = user;
        this.kernel = null;
        this.userTab = null;
        this.kernelTab = null;
        this.pane = null;
    }

    public InstructionViewerGroup(MIPSAssembledCodeViewer user, MIPSAssembledCodeViewer kernel, Tab userTab, Tab kernelTab, TabPane pane) {
        this.user = user;
        this.kernel = kernel;
        this.userTab = userTab;
        this.kernelTab = kernelTab;
        this.pane = pane;
    }

    public MIPSAssembledCodeViewer getUser() {
        return user;
    }

    public MIPSAssembledCodeViewer getKernel() {
        return kernel;
    }

    public boolean selectUser() {
        if (pane == null) return false;
        pane.getSelectionModel().select(userTab);
        return true;
    }

    public boolean selectKernel() {
        if (pane == null) return false;
        pane.getSelectionModel().select(kernelTab);
        return true;
    }
}
