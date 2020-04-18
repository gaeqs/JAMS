package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;

import java.util.List;

public class ConfigurationWindowNodeAction extends ConfigurationWindowNode<Action> {

	protected Action action;
	protected VBox combinations;

	public ConfigurationWindowNodeAction(Action action) {
		super(null, null, null, null);
		getStyleClass().add("configuration-window-node-actions");
		this.action = action;
		setAlignment(Pos.CENTER_LEFT);
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

	protected void init2() {
		Label label = new Label(action.getName());
		combinations = new VBox();


		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		getChildren().addAll(label, region, combinations);

		List<KeyCombination> keys = JamsApplication.getActionManager().getBindCombinations(action.getName());
		for (KeyCombination combination : keys) {
			combinations.getChildren().add(new TextField(combination.toString()));
		}
	}
}
