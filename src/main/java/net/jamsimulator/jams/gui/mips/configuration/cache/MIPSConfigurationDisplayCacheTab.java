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

package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

public class MIPSConfigurationDisplayCacheTab extends AnchorPane {

    public static final String STYLE_CLASS = "caches";

    private final MIPSSimulationConfiguration configuration;

    private final MIPSConfigurationCacheControls controls;
    private final MIPSConfigurationCacheContents contents;
    private final SplitPane splitPane;
    private final ScrollPane displayGroup;

    private MIPSConfigurationCacheDisplay display;

    public MIPSConfigurationDisplayCacheTab(MIPSSimulationConfiguration configuration) {
        this.configuration = configuration;

        getStyleClass().add(STYLE_CLASS);

        controls = new MIPSConfigurationCacheControls(this);
        splitPane = new SplitPane();
        displayGroup = new ScrollPane();
        displayGroup.setFitToHeight(true);
        displayGroup.setFitToWidth(true);

        var scroll = new PixelScrollPane();
        contents = new MIPSConfigurationCacheContents(null, this);
        scroll.setContent(contents);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);

        AnchorUtils.setAnchor(controls, 0, -1, 0, 0);
        AnchorUtils.setAnchor(splitPane, 30, 0, 0, 0);

        splitPane.getItems().addAll(scroll, displayGroup);
        getChildren().addAll(controls, splitPane);

        contents.selectFirst();

        Platform.runLater(() -> splitPane.setDividerPosition(0, 0.55));
    }

    public MIPSSimulationConfiguration getConfiguration() {
        return configuration;
    }

    public MIPSConfigurationCacheControls getControls() {
        return controls;
    }

    public MIPSConfigurationCacheContents getContents() {
        return contents;
    }

    public MIPSConfigurationCacheDisplay getDisplay() {
        return display;
    }

    public void display(CacheBuilder<?> builder) {
        display = builder == null ? null : new MIPSConfigurationCacheDisplay(builder);
        displayGroup.setContent(display);
    }
}
