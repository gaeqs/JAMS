package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.popup.ConfirmationWindow;
import net.jamsimulator.jams.language.Messages;

public class ActionExplorerActionCombination extends Button {

	private final ActionsExplorerAction action;
	private final KeyCombination combination;

	public ActionExplorerActionCombination(ActionsExplorerAction action, KeyCombination combination) {
		super(combination.toString());
		getStyleClass().add("action-remove-button");
		this.action = action;
		this.combination = combination;

		setOnAction(event -> ConfirmationWindow.open(Jams.getLanguageManager().getSelected()
				.getOrDefault(Messages.CONFIG_ACTION_UNBIND), this::deleteCombination, () -> {
		}));

	}

	private void deleteCombination() {
		JamsApplication.getActionManager().unbind(combination, action.getAction().getRegionTag());
		JamsApplication.getActionManager().save();
		action.refresh();
	}

}
