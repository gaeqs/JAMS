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

package net.jamsimulator.jams.gui.mips.editor;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionPopup;
import net.jamsimulator.jams.gui.editor.code.autocompletion.view.AutocompletionPopupBasicView;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.mips.editor.indexing.MIPSEditorIndex;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.LabelUtils;
import org.fxmisc.richtext.event.MouseOverTextEvent;

import java.time.Duration;
import java.util.Optional;

public class MIPSFileEditor extends CodeFileEditor {

    private final Popup popup;

    public MIPSFileEditor(FileEditorTab tab) {
        super(tab);

        popup = new Popup();
        autocompletionPopup = new AutocompletionPopup(
                this,
                new MIPSAutocompletionPopupController((MIPSProject) tab.getWorkingPane().getProjectTab().getProject()),
                new AutocompletionPopupBasicView()
        );//new MIPSAutocompletionPopup(this);
        //documentationPopup = new MIPSDocumentationPopup(this, (MIPSAutocompletionPopup) autocompletionPopup);

        applyAutoIndent();
        applyIndentRemoval();
        initializePopupListeners();
        applyLabelTabRemover();
    }

    public Optional<MIPSProject> getMipsProject() {
        return getProject() instanceof MIPSProject mProject ? Optional.of(mProject) : Optional.empty();
    }

    @Override
    public MIPSEditorIndex getIndex() {
        return (MIPSEditorIndex) super.getIndex();
    }

    @Override
    protected EditorIndex generateIndex() {
        return new MIPSEditorIndex(getProject(), tab.getFile().getName());
    }

    protected void applyAutoIndent() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isConsumed()) {
                int caretPosition = getCaretPosition();
                int currentLine = getCurrentParagraph();
                int currentColumn = getCaretColumn();
                if (currentLine < 1) return;

                String previous = getParagraph(currentLine).getText().substring(0, currentColumn);
                int labelIndex = LabelUtils.getLabelFinishIndex(previous);
                if (labelIndex != -1) {
                    previous = previous.substring(labelIndex + 1);
                }

                StringBuilder builder = new StringBuilder();
                for (char c : previous.toCharArray()) {
                    if (!Character.isWhitespace(c)) break;
                    builder.append(c);
                }

                insertText(caretPosition, "\n" + builder);
                event.consume();
            }
        });
    }

    private void applyIndentRemoval() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (getSelection().getLength() > 0) return;

                int currentLine = getCurrentParagraph();
                if (currentLine == 0) return;
                int caretPosition = getCaretColumn();
                var text = getParagraph(currentLine).substring(0, caretPosition);
                if (text.isEmpty() || !text.isBlank()) return;
                replaceText(currentLine, 0, currentLine, caretPosition, "");

                if (event.isControlDown()) {
                    // Avoid \n removal.
                    event.consume();
                }
            }
        });
    }

    private void initializePopupListeners() {
        setMouseOverTextDelay(Duration.ofMillis(300));
        addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, event -> {
            int index = event.getCharacterIndex();
            var optional = this.index.withLockF(false, i -> i.getElementAt(index));
            if (optional.isEmpty()) return;

            var content = new MIPSHoverInfo(optional.get());

            popup.getContent().clear();
            popup.getContent().add(content);

            var position = event.getScreenPosition();
            popup.show(this, position.getX(), position.getY() + 10);
        });

        addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            double x = event.getScreenX();
            double y = event.getScreenY();
            double treshold = 20;

            if (x < popup.getX() - treshold
                    || x > popup.getX() + popup.getWidth() + treshold
                    || y < popup.getY() - treshold
                    || y > popup.getY() + popup.getHeight() + treshold) {
                popup.hide();
            }
        });
    }

    private void applyLabelTabRemover() {
        addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(":")) {
                int column = getCaretColumn();
                int currentParagraph = getCurrentParagraph();
                var text = getParagraph(currentParagraph).getText();
                var trimmed = text.trim();
                if (trimmed.length() == 0) return;
                var offset = text.indexOf(trimmed.charAt(0));

                var label = trimmed.substring(0, trimmed.lastIndexOf(':') + 1) + text.substring(0, offset);
                replaceText(currentParagraph, 0, currentParagraph, column, label);
            }
        });
    }
}
