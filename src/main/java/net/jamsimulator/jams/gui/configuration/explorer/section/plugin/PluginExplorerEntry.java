package net.jamsimulator.jams.gui.configuration.explorer.section.plugin;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.plugin.Plugin;

public class PluginExplorerEntry extends HBox {

    public static final String STYLE_CLASS = "plugin-entry";
    public static final String SELECTED_STYLE_CLASS = "plugin-entry-selected";
    public static final String NAME_STYLE_CLASS = "plugin-entry-name";
    public static final String VERSION_STYLE_CLASS = "plugin-entry-version";
    public static final String AUTHOR_STYLE_CLASS = "plugin-entry-author";

    private final Plugin plugin;

    public PluginExplorerEntry(Plugin plugin, PluginExplorerList list) {
        this.plugin = plugin;

        getStyleClass().add(STYLE_CLASS);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        loadFavicon();
        loadCompactInfo();

        setOnMouseClicked(event -> list.select(this));
    }

    public Plugin getPlugin() {
        return plugin;
    }


    private void loadFavicon() {
        plugin.getFavicon().ifPresent(favicon ->
                getChildren().add(new NearestImageView(favicon, 60, 60)));
    }

    private void loadCompactInfo() {
        var pane = new GridPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.setHgap(10);
        pane.setVgap(5);

        var name = new Label(plugin.getName());
        name.getStyleClass().add(NAME_STYLE_CLASS);

        var version = new Label(plugin.getHeader().version());
        version.getStyleClass().add(VERSION_STYLE_CLASS);

        pane.add(name, 0, 0, 2, 1);
        pane.add(version, 0, 1, 1, 1);
        if (!plugin.getHeader().authors().isEmpty()) {
            var author = new Label(plugin.getHeader().authors().get(0));
            author.getStyleClass().add(AUTHOR_STYLE_CLASS);
            pane.add(author, 1, 1, 1, 1);
        }
        getChildren().add(pane);
    }
}
