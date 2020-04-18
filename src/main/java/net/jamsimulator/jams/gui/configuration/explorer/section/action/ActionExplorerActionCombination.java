package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;

public class ActionExplorerActionCombination extends Button {

	private KeyCombination combination;

	public ActionExplorerActionCombination(KeyCombination combination) {
		super(combination.toString());
		this.combination = combination;
	}

}
