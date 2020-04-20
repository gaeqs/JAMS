package net.jamsimulator.jams.gui.display.popup;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents an element inside a {@link AutocompletionPopup}.
 */
public class AutocompletionPopupElement extends HBox {

	private final String name;
	private final String autocompletion;

	/**
	 * Creates the element.
	 *
	 * @param name           the name the {@link AutocompletionPopup} is showing.
	 * @param autocompletion the replacement to place when the autocompletion is finished.
	 */
	public AutocompletionPopupElement(String name, String autocompletion) {
		getStyleClass().add("autocompletion-popup-element");
		this.name = name;
		this.autocompletion = autocompletion;
		getChildren().add(new Label(name));
	}

	/**
	 * Returns the name the {@link AutocompletionPopup} will show.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the replacement used by the {@link AutocompletionPopup} to autocomplete.
	 *
	 * @return the replacement.
	 */
	public String getAutocompletion() {
		return autocompletion;
	}
}
