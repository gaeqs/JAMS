package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;

import java.util.List;

public class ConfigurationWindowNodeAction extends ConfigurationWindowNode<Action> {

	protected Action action;
	protected VBox combinations;

	public ConfigurationWindowNodeAction(Action action) {
		super(null, null, null, null);
		this.action = action;
		init2();
	}


	@Override
	public Action getValue() {
		return action;
	}

	public void setValue(Action value) {
	}

	protected void saveValue(Action value) {
	}

	protected void init() {
	}

	protected void init2 () {
		Label label = new Label(action.getName());
		combinations = new VBox();
		getChildren().addAll(label, combinations);

		List<KeyCombination> keys = JamsApplication.getActionManager().getBindCombinations(action.getName());
		for (KeyCombination combination : keys) {
			combinations.getChildren().add(new Label(combination.toString()));
		}
	}
}
