package net.jamsimulator.jams.gui.configuration.explorer.section.plugin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.popup.ConfirmationWindow;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.StringStyler;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.plugin.Plugin;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

public class PluginExplorerDisplay extends AnchorPane {

    public static final String STYLE_CLASS = "plugin-display";
    public static final String NAME_STYLE_CLASS = "plugin-display-name";
    public static final String VERSION_STYLE_CLASS = "plugin-display-version";
    public static final String AUTHOR_STYLE_CLASS = "plugin-display-author";
    public static final String DESCRIPTION_STYLE_CLASS = "documentation";
    public static final String DESCRIPTION_STYLE_CLASS_2 = "plugin-display-description";

    private Plugin selected;

    public PluginExplorerDisplay() {
        getStyleClass().add(STYLE_CLASS);
        Jams.getLanguageManager().registerListeners(this, true);
    }

    public void display(Plugin plugin) {
        selected = plugin;
        getChildren().clear();
        if (plugin == null) return;
        loadHeader(plugin);
        loadDescription(plugin);
        loadDeleteButton(plugin);
    }

    private void loadHeader(Plugin plugin) {
        var hbox = new HBox();
        hbox.setPadding(new Insets(0, 5, 0, 0));
        hbox.setSpacing(5);

        plugin.getFavicon().ifPresent(favicon ->
                hbox.getChildren().add(new NearestImageView(favicon, 60, 60)));

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
        hbox.getChildren().add(pane);

        AnchorUtils.setAnchor(hbox, 0, -1, 0, 0);
        getChildren().add(hbox);
    }

    private void loadDescription(Plugin plugin) {
        if (plugin.getHeader().descriptionLanguageNode() == null) return;

        var display = new StyleClassedTextArea();
        display.getStyleClass().addAll(DESCRIPTION_STYLE_CLASS, DESCRIPTION_STYLE_CLASS_2);
        display.setEditable(false);

        var scroll = new VirtualizedScrollPane<>(display);

        var description = Jams.getLanguageManager().getSelected().getOrDefault(
                plugin.getHeader().descriptionLanguageNode());
        display.setWrapText(true);

        StringStyler.style(description, display);

        AnchorUtils.setAnchor(scroll, 60, 0, 0, 0);
        getChildren().add(scroll);
    }

    private void loadDeleteButton(Plugin plugin) {
        var view = new NearestImageView(JamsApplication.getIconManager()
                .getOrLoadSafe(Icons.CONTROL_REMOVE).orElse(null), 16, 16);

        var button = new Button("", view);
        button.getStyleClass().add("dark-bold-button");
        button.setTooltip(new LanguageTooltip(Messages.CONFIG_PLUGIN_UNINSTALL));


        button.setOnAction(event -> {
            var message = Jams.getLanguageManager().getSelected()
                    .getOrDefault(Messages.CONFIG_PLUGIN_UNINSTALL_CONFIRM)
                    .replace("{PLUGIN}", selected.getName());
            ConfirmationWindow.open(message, () -> Jams.getPluginManager().unistallPlugin(plugin),
                    () -> {
                    });
        });

        AnchorUtils.setAnchor(button, 0, -1, -1, 0);
        getChildren().add(button);
    }


    @Listener
    private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
        display(selected);
    }

    @Listener
    private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
        display(selected);
    }

}