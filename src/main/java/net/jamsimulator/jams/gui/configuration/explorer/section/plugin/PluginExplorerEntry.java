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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
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
                getChildren().add(new QualityImageView(favicon, 60, 60)));
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
