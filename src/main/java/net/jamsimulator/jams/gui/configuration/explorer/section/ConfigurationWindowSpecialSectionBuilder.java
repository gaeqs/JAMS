package net.jamsimulator.jams.gui.configuration.explorer.section;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;

public interface ConfigurationWindowSpecialSectionBuilder {

	ConfigurationWindowSection create(ConfigurationWindowExplorer explorer, ExplorerSection parent, String name,
									  String languageNode, int hierarchyLevel, Configuration configuration, Configuration meta);

}
