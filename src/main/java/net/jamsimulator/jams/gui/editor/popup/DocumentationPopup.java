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
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyledDocument;

/**
 * This class is a small guide to implement autocompletion popups.
 * <p>
 * It contains the default style and controls.
 */
public class DocumentationPopup extends Popup {

	protected final CodeFileEditor display;
	protected final StyleClassedTextArea content;

	protected VirtualizedScrollPane<StyleClassedTextArea> scroll;


	/**
	 * Creates the documentation popup.
	 *
	 * @param display the code display where this popup is displayed.
	 */
	public DocumentationPopup(CodeFileEditor display) {
		this.display = display;
		content = new StyleClassedTextArea();
		content.getStyleClass().add("documentation");

		scroll = new VirtualizedScrollPane<>(content);

		getContent().add(scroll);

		scroll.setPrefWidth(450);
		scroll.setPrefHeight(450);

		content.setWrapText(true);
		content.setEditable(false);

		var oldDispatcher = content.getEventDispatcher();
		content.setEventDispatcher((event, tail) -> {
			if(event instanceof MouseEvent) {
				return oldDispatcher.dispatchEvent(event, tail);
			}
			return display.getEventDispatcher().dispatchEvent(event, tail);
		});
	}

	/**
	 * Returns the {@link CodeFileEditor} where this popup is displayed.
	 *
	 * @return the {@link CodeFileEditor}.
	 */
	public CodeFileEditor getDisplay() {
		return display;
	}

	public void execute(int caretOffset) {
		int caretPosition = display.getCaretPosition() + caretOffset;
		if (caretPosition <= 0) return;

		if (isShowing()) return;
		Platform.runLater(() -> {
			Bounds bounds = display.getCaretBounds().orElse(null);
			if (bounds == null) return;
			show(display, bounds.getMinX() - getWidth(), bounds.getMinY() + 20);
			Platform.runLater(() -> {
				show(display, bounds.getMinX() - getWidth(), bounds.getMinY() + 20);
				requestFocus();
			});
		});
	}

}
