/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.editor.code.popup;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.editorold.CodeFileEditor;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

/**
 * This class is a small guide to implement autocompletion popups.
 * <p>
 * It contains the default style and controls.
 */
public class DocumentationPopup extends Popup {

    protected final CodeFileEditor display;
    protected final StyleClassedTextArea topMessage;
    protected final StyleClassedTextArea content;

    protected VirtualizedScrollPane<StyleClassedTextArea> scroll;


    /**
     * Creates the documentation popup.
     *
     * @param display the code display where this popup is displayed.
     */
    public DocumentationPopup(CodeFileEditor display) {
        this.display = display;
        topMessage = new StyleClassedTextArea();
        topMessage.getStyleClass().add("documentation-top-message");
        content = new StyleClassedTextArea();
        content.getStyleClass().add("documentation");

        scroll = new VirtualizedScrollPane<>(content);

        getContent().addAll(new VBox(topMessage, scroll));

        scroll.setPrefWidth(450);
        scroll.setPrefHeight(450);

        topMessage.setWrapText(true);
        topMessage.setEditable(false);
        content.setWrapText(true);
        content.setEditable(false);

        topMessage.setMaxHeight(50);

        manageDispatcher(content);
        manageDispatcher(topMessage);
    }

    private void manageDispatcher(StyleClassedTextArea area) {
        var oldDispatcher = area.getEventDispatcher();
        area.setEventDispatcher((event, tail) -> {
            if (event instanceof MouseEvent && ((MouseEvent) event).getButton() == MouseButton.PRIMARY) {
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
