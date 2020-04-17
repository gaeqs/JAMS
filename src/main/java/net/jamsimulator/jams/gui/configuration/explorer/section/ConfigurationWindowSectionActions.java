package net.jamsimulator.jams.gui.configuration.explorer.section;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNodeAction;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNodeString;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionLanguageRepresentation;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionRepresentation;

import java.util.Collections;
import java.util.List;

public class ConfigurationWindowSectionActions extends ConfigurationWindowSection {

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer       the {@link Explorer} of this section.
	 * @param parent         the {@link ExplorerSection} containing this section. This may be null.
	 * @param name           the name of the section.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ConfigurationWindowSectionActions(ConfigurationWindowExplorer explorer, ExplorerSection parent, String name,
											 String languageNode, int hierarchyLevel, Configuration configuration, Configuration meta) {
		super(explorer, parent, name, languageNode, hierarchyLevel, configuration, meta);
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

	@Override
	protected void loadChildren() {
		for (Action action : JamsApplication.getActionManager().getActions()) {
			nodes.add(new ConfigurationWindowNodeAction(action));
		}
	}

	public static class Builder implements ConfigurationWindowSpecialSectionBuilder {

		@Override
		public ConfigurationWindowSection create(ConfigurationWindowExplorer explorer, ExplorerSection parent, String name,
												 String languageNode, int hierarchyLevel, Configuration configuration, Configuration meta) {
			return new ConfigurationWindowSectionActions(explorer, parent, name, languageNode, hierarchyLevel, configuration, meta);
		}
	}
}
