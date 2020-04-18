package net.jamsimulator.jams.language.wrapper;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

public class LanguageTab extends Tab {

	private String node;

	public LanguageTab(String node) {
		this.node = node;
		Jams.getLanguageManager().registerListeners(this);
		refreshMessage();
	}

	public void dispose () {
		Jams.getLanguageManager().unregisterListeners(this);
	}

	private void refreshMessage() {
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
