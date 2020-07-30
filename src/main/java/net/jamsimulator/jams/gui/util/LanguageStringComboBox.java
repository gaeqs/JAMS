package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

import java.util.Arrays;
import java.util.Collection;

public abstract class LanguageStringComboBox extends ComboBox<String> {

	private final String[] nodes;

	public LanguageStringComboBox(String... nodes) {
		this(Arrays.asList(nodes));
	}

	public LanguageStringComboBox(Collection<String> nodes) {
		this.nodes = new String[nodes.size()];

		int i = 0;
		for (String node : nodes) {
			this.nodes[i] = node;
			getItems().add(Jams.getLanguageManager().getSelected().getOrDefault(node));
		}

		setOnAction(event -> onSelect(getSelectionModel().getSelectedIndex(), getSelectionModel().getSelectedItem()));
		Jams.getLanguageManager().registerListeners(this, true);
	}


	public abstract void onSelect(int index, String node);

	@Listener
	private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
		int selected = getSelectionModel().getSelectedIndex();
		for (int i = 0; i < nodes.length; i++) {
			getItems().set(i, Jams.getLanguageManager().getSelected().getOrDefault(nodes[i]));
		}
		getSelectionModel().select(selected);
	}

	@Listener
	private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
		int selected = getSelectionModel().getSelectedIndex();
		for (int i = 0; i < nodes.length; i++) {
			getItems().set(i, Jams.getLanguageManager().getSelected().getOrDefault(nodes[i]));
		}
		getSelectionModel().select(selected);
	}
}
