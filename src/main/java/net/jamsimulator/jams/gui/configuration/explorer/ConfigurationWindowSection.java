/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.configuration.explorer;

import javafx.scene.Node;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNodeBuilder;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNodeBuilders;
import net.jamsimulator.jams.gui.configuration.explorer.section.ConfigurationWindowSpecialSectionBuilder;
import net.jamsimulator.jams.gui.configuration.explorer.section.ConfigurationWindowSpecialSectionBuilders;
import net.jamsimulator.jams.gui.explorer.*;

import java.util.*;

public class ConfigurationWindowSection extends ExplorerSection {

	protected Configuration configuration, meta;
	protected List<ConfigurationWindowNode<?>> nodes;

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer       the {@link Explorer} of this section.
	 * @param parent         the {@link ExplorerSection} containing this section. This may be null.
	 * @param name           the name of the section.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ConfigurationWindowSection(ConfigurationWindowExplorer explorer, ExplorerSection parent, String name,
									  String languageNode, int hierarchyLevel, Configuration configuration, Configuration meta) {
		super(explorer, parent, name, hierarchyLevel, Comparator.comparing(ExplorerElement::getName));
		getStyleClass().add("configuration-window-section");
		this.configuration = configuration;
		this.meta = meta;

		this.nodes = new ArrayList<>();
		loadChildren();
		refreshAllElements();
		((ExplorerSectionLanguageRepresentation) representation).setLanguageNode(languageNode);
		representation.refreshStatusIcon();
	}

	/**
	 * Returns a unmodifiable {@link List} with all the
	 * {@link ConfigurationWindowNode} of this section.
	 *
	 * @return the unmodifiable {@link List}.
	 */
	public List<ConfigurationWindowNode<?>> getNodes() {
		return Collections.unmodifiableList(nodes);
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

	@Override
	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
	}

	protected void loadChildren() {
		Map<String, Object> map = configuration.getAll(false);
		map.forEach(this::manageChildrenAddition);
	}

	protected void manageChildrenAddition(String name, Object value) {
		if (value instanceof Configuration) {
			manageSectionAddition(name, (Configuration) value);
		} else {
			manageBasicObjectAddition(name, value);
		}
	}

	protected void manageSectionAddition(String name, Configuration value) {
		Optional<Configuration> metaConfig = this.meta == null ? Optional.empty() : this.meta.get(name);
		String languageNode = null;
		String special = null;

		if (metaConfig.isPresent()) {
			Optional<Configuration> metaOptional = metaConfig.get().get("meta");
			if (metaOptional.isPresent()) {
				ConfigurationMetadata meta = new ConfigurationMetadata(metaOptional.get());
				languageNode = meta.getLanguageNode();
				special = meta.getType();
			}
		}

		if (special != null) {
			Optional<ConfigurationWindowSpecialSectionBuilder> builder = ConfigurationWindowSpecialSectionBuilders.getByName(special);

			if (builder.isPresent()) {
				ExplorerElement element = builder.get().create(getExplorer(), this, name, languageNode,
						hierarchyLevel + 1, value, metaConfig.orElse(null));
				elements.add(element);
				filteredElements.add(element);
				return;
			}
		}
		ExplorerElement element = new ConfigurationWindowSection(getExplorer(), this, name, languageNode,
				hierarchyLevel + 1, value, metaConfig.orElse(null));
		elements.add(element);
		filteredElements.add(element);
	}

	protected void manageBasicObjectAddition(String name, Object value) {
		Optional<Configuration> metaOptional = meta == null ? Optional.empty() : meta.get(name);
		Optional<ConfigurationWindowNodeBuilder<?>> builder;
		String languageNode = null;
		if (metaOptional.isPresent()) {
			ConfigurationMetadata meta = new ConfigurationMetadata(metaOptional.get());

			languageNode = meta.getLanguageNode();

			builder = ConfigurationWindowNodeBuilders.getByName(meta.getType());
			if (builder.isPresent()) {
				nodes.add(builder.get().create(configuration, name, languageNode));
				return;
			}
		}
		builder = ConfigurationWindowNodeBuilders.getByType(value.getClass());
		if (builder.isPresent())
			nodes.add(builder.get().create(configuration, name, languageNode));
	}
}
