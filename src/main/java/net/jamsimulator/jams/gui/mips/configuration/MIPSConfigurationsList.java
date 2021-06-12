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

package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;

public class MIPSConfigurationsList extends AnchorPane {

    private final MIPSConfigurationWindow window;

    private final MIPSConfigurationListControls controls;
    private final MIPSConfigurationsListContents contents;

    public MIPSConfigurationsList(MIPSConfigurationWindow window) {
        this.window = window;

        SplitPane.setResizableWithParent(this, false);

        controls = new MIPSConfigurationListControls(this);

        var scroll = new PixelScrollPane();
        contents = new MIPSConfigurationsListContents(scroll, window);
        scroll.setContent(contents);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);

        AnchorUtils.setAnchor(controls, 0, -1, 0, 0);
        AnchorUtils.setAnchor(scroll, 30, 0, 0, 0);

        getChildren().addAll(controls, scroll);
    }

    public MIPSConfigurationWindow getWindow() {
        return window;
    }

    public MIPSConfigurationListControls getControls() {
        return controls;
    }

    public MIPSConfigurationsListContents getContents() {
        return contents;
    }
}
