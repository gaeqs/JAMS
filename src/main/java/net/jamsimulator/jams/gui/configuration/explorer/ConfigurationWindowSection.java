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

import javafx.scene.Node;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.configuration.explorer.section.ConfigurationWindowSpecialSectionBuilders;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.LanguageExplorerSection;

import java.util.*;

public class ConfigurationWindowSection extends LanguageExplorerSection {

    protected List<ConfigurationWindowNode> nodes;
    protected Map<String, Integer> regions;

    /**
     * Creates the explorer section.
     *
     * @param explorer       the {@link Explorer} of this section.
     * @param parent         the {@link ExplorerSection} containing this section. This may be null.
     * @param name           the name of the section.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     */
    public ConfigurationWindowSection(
            ConfigurationWindowExplorer explorer,
            ExplorerSection parent,
            String name,
            String languageNode,
            int hierarchyLevel,
            Configuration data,
            Configuration meta,
            Map<String, Integer> regions
    ) {
        super(explorer, parent, name, hierarchyLevel, Comparator.comparing(ExplorerElement::getName), languageNode);

        this.nodes = new ArrayList<>();
        this.regions = regions;
        loadChildren(data, meta);
        refreshAllElements();
        representation.refreshStatusIcon();
    }

    /**
     * Returns an unmodifiable {@link List} with all the
     * {@link ConfigurationWindowNode} of this section.
     *
     * @return the unmodifiable {@link List}.
     */
    public List<ConfigurationWindowNode> getNodes() {
        nodes.sort(Comparator.comparingInt(o -> o.getMetadata().getRegion() == null ?
                Integer.MAX_VALUE : regions.getOrDefault(o.getMetadata().getRegion(), Integer.MAX_VALUE)));
        return Collections.unmodifiableList(nodes);
    }

    public Map<String, Integer> getRegions() {
        return regions;
    }

    public Node getSpecialNode() {
        return null;
    }

    public boolean isSpecial() {
        return false;
    }

    @Override
    public ConfigurationWindowExplorer getExplorer() {
        return (ConfigurationWindowExplorer) super.getExplorer();
    }

    @Override
    protected void loadListeners() {
        super.loadListeners();
        setOnMouseClickedEvent(event -> getExplorer().getConfigurationWindow().display(this));
    }

    protected void loadChildren(Configuration data, Configuration meta) {
        meta.getAll(false).forEach((name, value) -> manageChildrenAddition(data, name, value));
    }

    protected void manageChildrenAddition(Configuration data, String name, Object value) {
        if (name.equals("meta") || !(value instanceof Configuration config)) return;
        var metaNode = (Configuration) config.get("meta").orElse(null);
        if (metaNode == null) {
            // Basic object
            manageBasicObjectAddition(data, name, config);
        } else {
            manageSectionAddition(name, data, config, metaNode);
        }
    }

    protected void manageSectionAddition(String name,
                                         Configuration parentDataNode,
                                         Configuration metaNode,
                                         Configuration sectionMeta) {

        var dataNode = parentDataNode.getOrCreateConfiguration(name);
        var meta = new ConfigurationMetadata(sectionMeta);

        if (meta.getType() != null) {
            // SPECIAL
            var builder = ConfigurationWindowSpecialSectionBuilders.getByName(meta.getType());
            if (builder.isPresent()) {
                var element = builder.get().create(getExplorer(),
                        this, name, meta.getLanguageNode(),
                        hierarchyLevel + 1, dataNode, metaNode, regions);
                elements.add(element);
                filteredElements.add(element);
                return;
            }
        }

        ExplorerElement element = new ConfigurationWindowSection(getExplorer(), this, name,
                meta.getLanguageNode(), hierarchyLevel + 1, dataNode, metaNode, regions);
        elements.add(element);
        filteredElements.add(element);

    }

    protected void manageBasicObjectAddition(Configuration data, String name, Configuration metaNode) {
        nodes.add(new ConfigurationWindowNode(data, name, new ConfigurationMetadata(metaNode)));
    }
}
