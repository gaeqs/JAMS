package net.jamsimulator.jams.gui.configuration.explorer;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.explorer.*;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNodeBuilder;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNodeBuilders;

import java.util.*;

public class ConfigurationWindowSection extends ExplorerSection {

	protected String languageNode;
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
		this.languageNode = languageNode;

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

	private void loadChildren() {
		Map<String, Object> map = configuration.getAll(false);
		map.forEach(this::manageChildrenAddition);
	}

	private void manageChildrenAddition(String name, Object value) {
		if (value instanceof Configuration) {
			Optional<Configuration> metaConfig = this.meta == null ? Optional.empty() : this.meta.get(name);
			String languageNode = null;

			if (metaConfig.isPresent()) {
				Optional<Configuration> metaOptional = metaConfig.get().get("meta");
				if (metaOptional.isPresent()) {
					ConfigurationMetadata meta = new ConfigurationMetadata(metaOptional.get());
					languageNode = meta.getLanguageNode();
				}
			}

			elements.add(new ConfigurationWindowSection(getExplorer(), this, name, languageNode,
					hierarchyLevel + 1, (Configuration) value, metaConfig.orElse(null)));
			return;
		}

		Optional<Configuration> metaOptional = meta.get(name);
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
