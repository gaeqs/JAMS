package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.explorer.*;
import net.jamsimulator.jams.language.Messages;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the main section of an {@link ActionsExplorer}.
 */
public class ActionsExplorerMainSection extends ExplorerSection {

	protected Map<String, ActionExplorerRegion> regions;

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer the {@link Explorer} of this section.
	 */
	public ActionsExplorerMainSection(ActionsExplorer explorer) {
		super(explorer, null, "Actions", 0, Comparator.comparing(ExplorerElement::getName));
		((ExplorerSectionLanguageRepresentation) representation).setLanguageNode(Messages.CONFIG_ACTION);
		generateRegions();
	}

	/**
	 * Adds an {@link Action} to the explorer.
	 * <p>
	 * If the {@link Action} was already inside the explorer a duplicated element will be created. Be careful!
	 *
	 * @param action the {@link Action}.
	 */
	public void addAction(Action action) {
		ActionExplorerRegion region;
		if (regions.containsKey(action.getRegionTag())) {
			region = regions.get(action.getRegionTag());
		} else {
			region = new ActionExplorerRegion((ActionsExplorer) explorer, this, action.getRegionTag());
			regions.put(action.getRegionTag(), region);
			addElement(region);
		}
		region.addElement(new ActionsExplorerAction(region, action));
	}

	/**
	 * Removes an {@link Action} from the explorer.
	 *
	 * @param action the {@link Action}.
	 */
	public void removeAction(Action action) {
		if (!regions.containsKey(action.getRegionTag())) return;
		ActionExplorerRegion region = regions.get(action.getRegionTag());

		region.removeElementIf(target -> {
			if (target instanceof ActionsExplorerAction
					&& ((ActionsExplorerAction) target).getAction().equals(action)) {
				((ActionsExplorerAction) target).dispose();
				return true;
			}
			return false;
		});

		if (region.isEmpty()) {
			regions.remove(action.getRegionTag());
			removeElement(region);
			region.dispose();
		}
	}

	protected void generateRegions() {
		regions = new HashMap<>();
		JamsApplication.getActionManager().getAll().forEach(this::addAction);
	}

	@Override
	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
	}
}
