package net.jamsimulator.jams.language.wrapper;

import javafx.application.Platform;
import javafx.scene.control.Button;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

public class LanguageButton extends Button {

	private String node;

	public LanguageButton(String node) {
		this.node = node;
		Jams.getLanguageManager().registerListeners(this);
		refreshMessage();
	}

	public void setNode(String node) {
		this.node = node;
		refreshMessage();
	}

	public void dispose () {
		Jams.getLanguageManager().unregisterListeners(this);
	}

	private void refreshMessage() {
		if (node == null) return;
		setText(Jams.getLanguageManager().getSelected().getOrDefault(node));
	}

	@Listener
	public void onSelectedLanguageChange(SelectedLanguageChangeEvent.After event) {
		Platform.runLater(this::refreshMessage);
	}

	@Listener
	public void onDefaultLanguageChange(DefaultLanguageChangeEvent.After event) {
		Platform.runLater(this::refreshMessage);
	}
}
