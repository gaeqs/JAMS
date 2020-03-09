package net.jamsimulator.jams.gui.settings.explorer;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.settings.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.settings.explorer.node.ConfigurationWindowNodeBoolean;

import java.util.*;

public class ConfigurationWindowSection extends ExplorerSection {

	protected Configuration configuration;
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
									  Configuration configuration) {
		super(explorer, parent, name, hierarchyLevel, Comparator.comparing(ExplorerElement::getName));
		this.configuration = configuration;

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
		Map<String, Object> map = configuration.getAll();
		map.forEach(this::manageChildrenAddition);
	}

	private void manageChildrenAddition(String name, Object value) {
		if (value instanceof Configuration) {
			elements.add(new ConfigurationWindowSection(getExplorer(), this, name,
					hierarchyLevel + 1, (Configuration) value));
			return;
		}

		//NODES
		if (value instanceof Boolean)
			nodes.add(new ConfigurationWindowNodeBoolean(configuration, name, null, false));
	}
}
