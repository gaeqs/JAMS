package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ActionsExplorerMainSection extends ExplorerSection {

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer the {@link Explorer} of this section.
	 */
	public ActionsExplorerMainSection(ActionsExplorer explorer) {
		super(explorer, null, "Actions", 0, Comparator.comparing(ExplorerElement::getName));
		generateRegions();
	}

	protected void generateRegions() {
		Map<String, ActionExplorerRegion> regions = new HashMap<>();
		ActionExplorerRegion region;
		for (Action action : JamsApplication.getActionManager().getActions()) {
			if (regions.containsKey(action.getRegionTag())) {
				region = regions.get(action.getRegionTag());
			} else {
				region = new ActionExplorerRegion((ActionsExplorer) explorer, this, action.getRegionTag());
				regions.put(action.getRegionTag(), region);
				addElement(region);
			}
			region.addElement(new ActionsExplorerAction(region, action));
		}
	}
}
