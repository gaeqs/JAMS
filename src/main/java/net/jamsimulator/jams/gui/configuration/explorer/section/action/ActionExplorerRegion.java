package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import net.jamsimulator.jams.gui.explorer.*;
import net.jamsimulator.jams.manager.ActionManager;

import java.util.Comparator;

/**
 * Represents an {@link net.jamsimulator.jams.gui.action.Action} region inside the explorer.
 */
public class ActionExplorerRegion extends ExplorerSection {

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer the {@link Explorer} of this section.
	 * @param parent   the {@link ExplorerSection} containing this section. This may be null.
	 * @param region   the region.
	 */
	public ActionExplorerRegion(ActionsExplorer explorer, ExplorerSection parent, String region) {
		super(explorer, parent, region, 1, Comparator.comparing(ExplorerElement::getName));
		((ExplorerSectionLanguageRepresentation) representation).setLanguageNode(ActionManager.LANGUAGE_REGION_NODE_PREFIX + region);
	}

	/**
	 * Disposes this element, removing all listeners.
	 * This should be called when this element is not longer needed.
	 */
	public void dispose() {
		((ExplorerSectionLanguageRepresentation) representation).dispose();
	}


	@Override
	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
	}

}
