package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.popup.ConfirmationWindow;

public class ActionExplorerActionCombinationAdd extends Button {

	private final ActionsExplorerAction action;

	public ActionExplorerActionCombinationAdd(ActionsExplorerAction action) {
		super("+");
		this.action = action;

		setOnAction(event -> ConfirmationWindow.open("ADD",
				() -> {}, () -> { }));

	}

}
