package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import net.jamsimulator.jams.gui.explorer.*;

import java.util.Comparator;

public class ActionExplorerRegion extends ExplorerSection {

	public static final String LANGUAGE_NODE_PREFIX = "ACTION_REGION_";

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer the {@link Explorer} of this section.
	 * @param parent   the {@link ExplorerSection} containing this section. This may be null.
	 * @param region   the region.
	 */
	public ActionExplorerRegion(ActionsExplorer explorer, ExplorerSection parent, String region) {
		super(explorer, parent, region, 1, Comparator.comparing(ExplorerElement::getName));
		((ExplorerSectionLanguageRepresentation) representation).setLanguageNode(LANGUAGE_NODE_PREFIX + region);
	}


	@Override
	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
	}

}
