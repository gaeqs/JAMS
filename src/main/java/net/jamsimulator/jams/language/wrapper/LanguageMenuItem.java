package net.jamsimulator.jams.language.wrapper;

import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

public class LanguageMenuItem extends MenuItem {

	private String node;

	public LanguageMenuItem(String node) {
		this.node = node;
		Jams.getLanguageManager().registerListeners(this);
		refreshMessage();
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
