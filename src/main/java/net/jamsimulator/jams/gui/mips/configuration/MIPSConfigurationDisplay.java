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

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.mips.configuration.cache.MIPSConfigurationDisplayCacheTab;
import net.jamsimulator.jams.gui.mips.configuration.syscall.MIPSConfigurationDisplaySyscallTab;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

public class MIPSConfigurationDisplay extends AnchorPane {

    public static final String STYLE_CLASS = "display";

    private final MIPSConfigurationWindow window;
    private final MIPSSimulationConfiguration configuration;

    public MIPSConfigurationDisplay(MIPSConfigurationWindow window, MIPSSimulationConfiguration configuration) {
        this.window = window;
        this.configuration = configuration;

        getStyleClass().add(STYLE_CLASS);
        AnchorUtils.setAnchor(this, 5, 5, 5, 5);

        populate();
    }

    public MIPSConfigurationWindow getWindow() {
        return window;
    }

    public MIPSSimulationConfiguration getConfiguration() {
        return configuration;
    }

    private void populate() {
        loadNameField();
        loadTabs();
    }

    private void loadNameField() {
        var nameField = new MIPSConfigurationDisplayNameField(window, configuration);
        AnchorUtils.setAnchor(nameField, 0, -1, 0, 0);
        getChildren().add(nameField);
    }

    private void loadTabs() {
        var tabPane = new TabPane();
        AnchorUtils.setAnchor(tabPane, 40, 0, 0, 0);
        getChildren().add(tabPane);

        loadGeneralTab(tabPane);
        loadSyscallsTab(tabPane);
        loadCacheTab(tabPane);
    }

    private void loadGeneralTab(TabPane tabPane) {
        Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_GENERAL);
        tab.setClosable(false);

        ScrollPane scrollPane = new PixelScrollPane(new MIPSConfigurationDisplayGeneralTab(configuration));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    private void loadSyscallsTab(TabPane tabPane) {
        Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB);
        tab.setClosable(false);
        tab.setContent(new MIPSConfigurationDisplaySyscallTab(configuration));
        tabPane.getTabs().add(tab);
    }

    private void loadCacheTab(TabPane tabPane) {
        Tab tab = new LanguageTab(Messages.SIMULATION_CONFIGURATION_CACHES_TAB);
        tab.setClosable(false);
        tab.setContent(new MIPSConfigurationDisplayCacheTab(configuration));
        tabPane.getTabs().add(tab);
    }
}
