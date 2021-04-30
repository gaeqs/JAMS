package net.jamsimulator.jams.gui.configuration.explorer.section.plugin;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.plugin.event.PluginRegisterEvent;
import net.jamsimulator.jams.plugin.event.PluginUnregisterEvent;

public class PluginExplorerList extends VBox {

    public static final String STYLE_CLASS = "plugin-list";

    private final ConfigurationWindowSectionPlugins section;

    private PluginExplorerEntry selected;

    public PluginExplorerList(ConfigurationWindowSectionPlugins section) {
        this.section = section;

        getStyleClass().add(STYLE_CLASS);
        setAlignment(Pos.TOP_CENTER);

        Jams.getPluginManager().forEach(plugin -> getChildren().add(new PluginExplorerEntry(plugin, this)));
        loadInstallButton();

        Jams.getPluginManager().registerListeners(this, true);
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
        var view = new NearestImageView(JamsApplication.getIconManager()
                .getOrLoadSafe(Icons.CONTROL_ADD).orElse(null), 16, 16);

        var button = new Button("", view);
        button.getStyleClass().add("dark-bold-button");
        button.setTooltip(new LanguageTooltip(Messages.CONFIG_PLUGIN_INSTALL));

        button.setOnAction(event -> {

            var chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plugin", ".jar"));
            var file = chooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                Jams.getPluginManager().installPLugin(file);
            }
        });

        getChildren().add(button);
    }

    @Listener
    private void onPluginRegister(PluginRegisterEvent.After event) {
        getChildren().add(getChildren().size() - 1, new PluginExplorerEntry(event.getPlugin(), this));
    }

    @Listener
    private void onPluginUnregister(PluginUnregisterEvent.After event) {
        getChildren().removeIf(target -> target instanceof PluginExplorerEntry
                && ((PluginExplorerEntry) target).getPlugin() == event.getPlugin());
        if (selected.getPlugin() == event.getPlugin()) select(null);
    }
}
