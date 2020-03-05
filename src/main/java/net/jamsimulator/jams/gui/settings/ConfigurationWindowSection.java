package net.jamsimulator.jams.gui.settings;

import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;

import java.util.Comparator;

public class ConfigurationWindowSection extends ExplorerSection {

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer       the {@link Explorer} of this section.
	 * @param parent         the {@link ExplorerSection} containing this section. This may be null.
	 * @param name           the name of the section.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 * @param comparator     the comparator used to sort the elements.
	 */
	public ConfigurationWindowSection(Explorer explorer, ExplorerSection parent, String name, int hierarchyLevel, Comparator<ExplorerElement> comparator) {
		super(explorer, parent, name, hierarchyLevel, comparator);
	}
}
