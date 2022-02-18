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

package net.jamsimulator.jams.gui.configuration.explorer.section.plugin;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.plugin.Plugin;
import net.jamsimulator.jams.plugin.PluginManager;

public class PluginExplorerList extends VBox {

    public static final String STYLE_CLASS = "plugin-list";

    private final ConfigurationWindowSectionPlugins section;

    private PluginExplorerEntry selected;

    public PluginExplorerList(ConfigurationWindowSectionPlugins section) {
        this.section = section;

        getStyleClass().add(STYLE_CLASS);
        setAlignment(Pos.TOP_CENTER);
        loadInstallButton();

        Manager.of(Plugin.class).forEach(plugin -> getChildren().add(new PluginExplorerEntry(plugin, this)));
        Manager.of(Plugin.class).registerListeners(this, true);
    }

    void select(PluginExplorerEntry entry) {
        if (selected != null) {
            selected.getStyleClass().remove(PluginExplorerEntry.SELECTED_STYLE_CLASS);
        }
        selected = entry;
        section.display.display(selected == null ? null : selected.getPlugin());
        if (selected != null) {
            selected.getStyleClass().add(PluginExplorerEntry.SELECTED_STYLE_CLASS);
        }
    }

    private void loadInstallButton() {
        var view = new QualityImageView(Icons.CONTROL_ADD, 16, 16);

        var button = new Button("", view);
        button.getStyleClass().add("button-dark-bold");
        button.setTooltip(new LanguageTooltip(Messages.CONFIG_PLUGIN_INSTALL));

        button.setOnAction(event -> {

            var chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plugin", "*.jar"));
            var file = chooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                Manager.get(PluginManager.class).installPLugin(file);
            }
        });

        getChildren().add(button);
    }

    @Listener
    private void onPluginRegister(ManagerElementRegisterEvent.After<Plugin> event) {
        if (getChildren().stream().anyMatch(
                it -> it instanceof PluginExplorerEntry e && e.getPlugin().equals(event.getElement())))
            return;
        getChildren().add(getChildren().size() - 1, new PluginExplorerEntry(event.getElement(), this));
    }

    @Listener
    private void onPluginUnregister(ManagerElementUnregisterEvent.After<Plugin> event) {
        getChildren().removeIf(target -> target instanceof PluginExplorerEntry
                && ((PluginExplorerEntry) target).getPlugin() == event.getElement());
        if (selected != null && selected.getPlugin() == event.getElement()) select(null);
    }
}
