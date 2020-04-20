package net.jamsimulator.jams.gui.display.popup;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AutocompletionPopupElement extends HBox {

	private final String autocompletion;

	public AutocompletionPopupElement(String name, String autocompletion) {
		getStyleClass().add("autocompletion-popup-element");
		this.autocompletion = autocompletion;
		getChildren().add(new Label(name));
	}

	public String getAutocompletion() {
		return autocompletion;
	}
}
