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

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.configuration.explorer.section.ConfigurationWindowSpecialSectionBuilder;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionLanguageRepresentation;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionRepresentation;
import net.jamsimulator.jams.gui.util.PixelScrollPane;

import java.util.List;
import java.util.Map;

/**
 * Represents a special {@link ConfigurationWindowSection} that contains all actions' configuration.
 */
public class ConfigurationWindowSectionPlugins extends ConfigurationWindowSection {

    public static final String STYLE_CLASS = "plugin-section";
    final PluginExplorerList list;
    final PluginExplorerDisplay display;
    private final SplitPane pane;

    /**
     * Creates the plugins' explorer section.
     *
     * @param explorer       the {@link Explorer} of this section.
     * @param parent         the {@link ExplorerSection} containing this section. This may be null.
     * @param name           the name of the section.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     */
    public ConfigurationWindowSectionPlugins(ConfigurationWindowExplorer explorer,
                                             ExplorerSection parent, String name,
                                             String languageNode, int hierarchyLevel,
                                             Configuration configuration,
                                             Configuration meta,
                                             Map<String, Integer> regions) {
        super(explorer, parent, name, languageNode, hierarchyLevel, configuration, meta, regions);

        pane = new SplitPane();
        list = new PluginExplorerList(this);
        display = new PluginExplorerDisplay();

        pane.getStyleClass().add(STYLE_CLASS);

        var listScrollPane = new PixelScrollPane(list);
        listScrollPane.setFitToHeight(true);
        listScrollPane.setFitToWidth(true);

        pane.getItems().addAll(listScrollPane, display);
    }

    @Override
    public List<ConfigurationWindowNode> getNodes() {
        return null;
    }

    @Override
    public Node getSpecialNode() {
        return pane;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    protected ExplorerSectionRepresentation loadRepresentation() {
        return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
    }

    @Override
    protected void loadChildren() {
    }

    public static class Builder implements ConfigurationWindowSpecialSectionBuilder {

        @Override
        public ConfigurationWindowSection create(ConfigurationWindowExplorer explorer, ExplorerSection parent, String name,
                                                 String languageNode, int hierarchyLevel, Configuration configuration,
                                                 Configuration meta, Map<String, Integer> regions) {
            return new ConfigurationWindowSectionPlugins(explorer, parent, name, languageNode, hierarchyLevel, configuration, meta, regions);
        }
    }
}
