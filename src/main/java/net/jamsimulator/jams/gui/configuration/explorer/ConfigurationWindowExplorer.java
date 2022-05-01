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

package net.jamsimulator.jams.gui.configuration.explorer;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.configuration.ConfigurationWindow;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.plugin.Plugin;

import java.util.HashMap;

public class ConfigurationWindowExplorer extends Explorer {

    public static final String STYLE_CLASS = "configuration-explorer";

    private final ConfigurationWindow configurationWindow;

    /**
     * Creates a settings explorer.
     */
    public ConfigurationWindowExplorer(ConfigurationWindow configurationWindow, ScrollPane scrollPane) {
        super(scrollPane, false, false);
        getStyleClass().add(STYLE_CLASS);
        this.configurationWindow = configurationWindow;
        generateMainSection();
        Manager.of(Plugin.class).registerListeners(this, true);
    }

    public ConfigurationWindow getConfigurationWindow() {
        return configurationWindow;
    }

    @Override
    protected void generateMainSection() {
        mainSection = new ConfigurationWindowSection(
                this,
                null,
                "Configuration",
                Messages.CONFIG,
                0,
                configurationWindow.getConfiguration().data(),
                configurationWindow.getConfiguration().metadata(),
                new HashMap<>()
        );
        getChildren().add(mainSection);
        mainSection.expand();
        hideMainSectionRepresentation();
    }

    @Override
    public void selectElementAlone(ExplorerElement element) {
        super.selectElementAlone(element);
        if (element instanceof ConfigurationWindowSection) {
            configurationWindow.display((ConfigurationWindowSection) element);
        }
    }

    private void refresh() {
        getChildren().clear();
        generateMainSection();
    }

    @Listener
    private void onPluginLoad(ManagerElementRegisterEvent.After<Plugin> event) {
        refresh();
    }

    @Listener
    private void onPluginUnload(ManagerElementUnregisterEvent.After<Plugin> event) {
        refresh();
    }
}
