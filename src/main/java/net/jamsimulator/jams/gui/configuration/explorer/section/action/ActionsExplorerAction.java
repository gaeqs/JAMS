package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSeparatorRegion;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.manager.ActionManager;

import java.util.List;

public class ActionsExplorerAction extends ExplorerBasicElement {

	public static final int MAX_SHORTCUTS = 3;

	protected Action action;
	protected Region bigSeparator;

	/**
	 * Creates an action explorer element.
	 *
	 * @param parent the {@link ExplorerSection} containing this element.
	 * @param action the represented {@link Action}.
	 */
	public ActionsExplorerAction(ActionExplorerRegion parent, Action action) {
		super(parent, action.getName(), 2);
		this.action = action;
		((LanguageLabel) label).setNode(ActionManager.LANGUAGE_NODE_PREFIX + action.getName());

		bigSeparator = new Region();
		HBox.setHgrow(bigSeparator, Priority.ALWAYS);
		getChildren().add(bigSeparator);

		JamsApplication.getActionManager().registerListeners(this);

		refresh();
	}

	public Action getAction() {
		return action;
	}


	public void dispose() {
		JamsApplication.getActionManager().unregisterListeners(this);
	}

	public void refresh() {
		getChildren().clear();
		getChildren().addAll(separator, icon, label, bigSeparator);

		List<KeyCombination> combinations = JamsApplication.getActionManager().getBindCombinations(action.getName());
		for (KeyCombination combination : combinations) {
			getChildren().add(new ActionExplorerActionCombination(this, combination));
		}
		if (combinations.size() < MAX_SHORTCUTS) {
			getChildren().add(new ActionExplorerActionCombinationAdd(this));
		}
	}

	@Override
	protected void loadElements() {
		icon = new NearestImageView();
		label = new LanguageLabel(null);

		separator = new ExplorerSeparatorRegion(false, hierarchyLevel);

		setSpacing(SPACING);
		setPadding(new Insets(0, 5, 0, 0));
		setAlignment(Pos.CENTER_LEFT);
	}

	@Listener
	private void onCombinationBind(ActionBindEvent.After event) {
		if (action.equals(event.getAction()) || event.getReplacedActions().containsValue(action)) {
			refresh();
		}
	}

	@Listener
	private void onCombinationUnbind(ActionUnbindEvent.After event) {
		if (action.equals(event.getAction())) {
			refresh();
		}
	}
}
