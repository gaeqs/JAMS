package net.jamsimulator.jams.gui.settings.explorer;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.settings.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.settings.explorer.node.ConfigurationWindowNodeBuilder;
import net.jamsimulator.jams.gui.settings.explorer.node.ConfigurationWindowNodeBuilders;

import java.util.*;

public class ConfigurationWindowSection extends ExplorerSection {

	protected Configuration configuration, types;
	protected List<ConfigurationWindowNode<?>> nodes;

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer       the {@link Explorer} of this section.
	 * @param parent         the {@link ExplorerSection} containing this section. This may be null.
	 * @param name           the name of the section.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ConfigurationWindowSection(ConfigurationWindowExplorer explorer, ExplorerSection parent, String name, int hierarchyLevel,
									  Configuration configuration, Configuration types) {
		super(explorer, parent, name, hierarchyLevel, Comparator.comparing(ExplorerElement::getName));
		this.configuration = configuration;
		this.types = types;

		this.nodes = new ArrayList<>();
		loadChildren();
		refreshAllElements();
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

	protected void loadListeners() {
		super.loadListeners();
		setOnMouseClickedEvent(event -> getExplorer().getConfigurationWindow().display(this));
	}

	private void loadChildren() {
		Map<String, Object> map = configuration.getAll(false);
		map.forEach(this::manageChildrenAddition);
	}

	private void manageChildrenAddition(String name, Object value) {
		if (value instanceof Configuration) {

			Optional<Configuration> type = types == null ? Optional.empty() : types.get(name);

			elements.add(new ConfigurationWindowSection(getExplorer(), this, name,
					hierarchyLevel + 1, (Configuration) value, type.orElse(null)));
			return;
		}

		Optional<String> type = types.getString(name);
		if (type.isPresent()) {
			Optional<ConfigurationWindowNodeBuilder<?>> builder = ConfigurationWindowNodeBuilders.getByName(type.get());
			if (builder.isPresent()) {
				nodes.add(builder.get().create(configuration, name, null));
				return;
			}
		}

		ConfigurationWindowNodeBuilders.getByType(value.getClass()).ifPresent(configurationWindowNodeBuilder ->
				nodes.add(configurationWindowNodeBuilder.create(configuration, name, null)));
	}
}
