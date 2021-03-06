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

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.image.NearestImageView;

/**
 * Represents an element inside a {@link AutocompletionPopup}.
 */
public class AutocompletionPopupElement extends HBox {

	private final AutocompletionPopup popup;

	private int index;
	private final Object element;
	private final String name;
	private final String autocompletion;

	private final int offset;

	/**
	 * Creates the element.
	 *
	 * @param popup          the {@link AutocompletionPopup} where this element is inside of.
	 * @param index          the index of this element inside the popup.
	 * @param name           the name the {@link AutocompletionPopup} is showing.
	 * @param autocompletion the replacement to place when the autocompletion is finished.
	 */
	public AutocompletionPopupElement(AutocompletionPopup popup, Object element, int index, String name, String autocompletion, int offset, Image icon) {
		getStyleClass().add("autocompletion-popup-element");
		setMinWidth(500);
		this.popup = popup;
		this.index = index;
		this.element = element;
		this.name = name;
		this.autocompletion = autocompletion;
		this.offset = offset;

		getChildren().addAll(new NearestImageView(icon, 16, 16), new Label(name));

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
	 * Returns the element represented by this popup element.
	 *
	 * @return the element.
	 */
	public Object getElement() {
		return element;
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

	public int getOffset() {
		return offset;
	}
}
