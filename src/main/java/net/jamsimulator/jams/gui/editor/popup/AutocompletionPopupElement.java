/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.editor.popup;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents an element inside a {@link AutocompletionPopup}.
 */
public class AutocompletionPopupElement extends HBox {

	private final AutocompletionPopup popup;

	private int index;
	private final String name;
	private final String autocompletion;

	/**
	 * Creates the element.
	 *
	 * @param popup          the {@link AutocompletionPopup} where this element is inside of.
	 * @param index          the index of this element inside the popup.
	 * @param name           the name the {@link AutocompletionPopup} is showing.
	 * @param autocompletion the replacement to place when the autocompletion is finished.
	 */
	public AutocompletionPopupElement(AutocompletionPopup popup, int index, String name, String autocompletion) {
		getStyleClass().add("autocompletion-popup-element");
		this.popup = popup;
		this.index = index;
		this.name = name;
		this.autocompletion = autocompletion;
		getChildren().add(new Label(name));

		setOnMouseClicked(event -> {
			this.popup.select(this.index, false);
			event.consume();
		});
	}

	/**
	 * Returns the {@link AutocompletionPopup} where this element is inside of.
	 *
	 * @return the {@link AutocompletionPopup}.
	 */
	public AutocompletionPopup getPopup() {
		return popup;
	}

	/**
	 * Returns the index of this element.
	 *
	 * @return the index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Updates the index of this element.
	 *
	 * @param index the index.
	 */
	public void setIndex(int index) {
		this.index = index;
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
