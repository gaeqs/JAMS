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

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.mips.editor.index.MIPSEditorIndex;
import net.jamsimulator.jams.project.mips.MIPSProject;
import org.fxmisc.richtext.event.MouseOverTextEvent;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

public class MIPSFileEditor extends CodeFileEditor {

    private final Popup popup;

    public MIPSFileEditor(FileEditorTab tab) {
        super(tab);

        popup = new Popup();
        autocompletionPopup = new MIPSAutocompletionPopup(this);
        documentationPopup = new MIPSDocumentationPopup(this, (MIPSAutocompletionPopup) autocompletionPopup);

        applyAutoIndent();
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
    public void reformat() {
//        enableRefreshEvent(false);
//        String reformattedCode = new MIPSCodeFormatter(elements).format();
//        String text = getText();
//        if (reformattedCode.equals(text)) return;
//        int oLine = getCurrentParagraph();
//        int oColumn = getCaretColumn();
//
//        replaceText(0, text.length(), reformattedCode);
//
//        List<CodeFileLine> lines = getLines();
//
//        int newSize = lines.size();
//        int line = Math.min(oLine, newSize - 1);
//        int column = Math.min(oColumn, lines.get(line).getText().length());
//        moveTo(line, column);
//
//
//        double height = totalHeightEstimateProperty().getValue() == null ? 0 : totalHeightEstimateProperty().getValue();
//
//        double toPixel = height * line / newSize - getLayoutBounds().getHeight() / 2;
//        toPixel = Math.max(0, Math.min(height, toPixel));
//
//        scrollPane.scrollYBy(toPixel);
//        index(reformattedCode);
//        tab.setSaveMark(true);
//        tab.layoutDisplay();
//        enableRefreshEvent(true);
    }

    public boolean replaceAllText(String text) {
        replace(0, getText().length(), text, Collections.emptySet());
        return true;
    }

    @Override
    protected EditorIndex generateIndex() {
        return new MIPSEditorIndex(getProject());
    }

    protected void applyAutoIndent() {
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int caretPosition = getCaretPosition();
                int currentLine = offsetToPosition(caretPosition, Bias.Forward).getMajor();
                if (currentLine == -1) return;

                String previous = getParagraph(currentLine - 1).getText();

                var line = getIndex().getLine(currentLine - 1);
                if (line.getLabel().isPresent()) {
                    var label = line.getLabel().get();
                    previous = previous.substring(label.getLength());
                }

                StringBuilder builder = new StringBuilder();
                for (char c : previous.toCharArray()) {
                    if (c != '\t' && c != ' ') break;
                    builder.append(c);
                }

                Platform.runLater(() -> insertText(caretPosition, builder.toString()));
            }
        });
    }

    private void initializePopupListeners() {
        setMouseOverTextDelay(Duration.ofMillis(300));
        addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, event -> {
            int index = event.getCharacterIndex();
            var optional = this.index.getElementAt(index);
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
                int caretPosition = getCaretPosition();
                int currentParagraph = getCurrentParagraph();
                var line = getIndex().getLine(currentParagraph);

                if (line.getLabel().isEmpty()) return;
                var label = line.getLabel().get();
                if (label.getEnd() != caretPosition - 1) return;

                String text = label.getText();

                int i = 0;
                for (char c : text.toCharArray()) {
                    if (c != '\t' && c != ' ') break;
                    i++;
                }
                if (i == 0) return;

                String first = text.substring(0, i);
                String last = text.substring(i);
                replaceText(label.getStart(), label.getEnd() + 1, last + first);
            }
        });
    }
}
