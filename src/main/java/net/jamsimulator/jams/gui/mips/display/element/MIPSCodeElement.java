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

package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.display.MIPSEditorError;
import net.jamsimulator.jams.language.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a element inside a MIPS file.
 */
public abstract class MIPSCodeElement {

	protected int startIndex;
	protected int endIndex;
	protected final String text;

	protected List<MIPSEditorError> errors;

	/**
	 * Creates the element.
	 * <p>
	 * The start and end indices must be file absolute indices.
	 *
	 * @param startIndex the start index.
	 * @param endIndex   the end index.
	 * @param text       the text.
	 */
	public MIPSCodeElement(int startIndex, int endIndex, String text) {
		if (startIndex > endIndex) {
			throw new IllegalArgumentException("Start index (" + startIndex + ") is bigger than the end index (" + endIndex + ").");
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.text = text;
		this.errors = new ArrayList<>();
	}

	/**
	 * Returns the file absolute index where this element starts. This index is inclusive.
	 *
	 * @return the index.
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Returns the file absolute index where this element ends. This index is exclusive.
	 *
	 * @return the index.
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * Moves the element.
	 *
	 * @param offset the chars the element must move.
	 */
	public void move(int offset) {
		startIndex += offset;
		endIndex += offset;
	}

	/**
	 * Returns the text representing this element.
	 *
	 * @return the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the text representing this element without any children.
	 *
	 * @return the text.
	 */
	public abstract String getSimpleText();

	/**
	 * Returns the styles to apply to this element.
	 *
	 * @return the styles.
	 */
	public abstract List<String> getStyles();

	/**
	 * Returns whether this element has errors.
	 *
	 * @return whether this element has errors.
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * Populates the given popup with the errors inside this element.
	 *
	 * @param popup the {@link VBox} inside the popup.
	 */
	public void populatePopupWithErrors(VBox popup) {
		Language language = Jams.getLanguageManager().getSelected();

		errors.forEach(target -> {
			String message = language.getOrDefault("EDITOR_MIPS_ERROR_" + target);
			popup.getChildren().add(new Label(message.replace("{TEXT}", getText())));
		});
	}

	/**
	 * Refreshes the metadata of this element.
	 * <p>
	 * Metadata includes errors and global status for labels.
	 *
	 * @param elements the {@link MIPSFileElements}.
	 */
	public abstract void refreshMetadata(MIPSFileElements elements);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MIPSCodeElement that = (MIPSCodeElement) o;
		return startIndex == that.startIndex &&
				endIndex == that.endIndex;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startIndex, endIndex);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"startIndex=" + startIndex +
				", endIndex=" + endIndex +
				", text='" + text + '\'' +
				'}';
	}
}
