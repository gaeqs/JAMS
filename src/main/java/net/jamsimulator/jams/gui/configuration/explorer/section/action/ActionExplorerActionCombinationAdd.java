package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.Button;
import net.jamsimulator.jams.gui.popup.BindActionWindow;

public class ActionExplorerActionCombinationAdd extends Button {

	public ActionExplorerActionCombinationAdd(ActionsExplorerAction action) {
		super("+");
		setOnAction(event -> BindActionWindow.open(action.getAction()));
	}

}
