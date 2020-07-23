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

package net.jamsimulator.jams.gui.mips.editor;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.CodeFileLine;
import net.jamsimulator.jams.gui.editor.FileEditorTab;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLabel;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSLine;
import net.jamsimulator.jams.gui.mips.project.MipsStructurePane;
import net.jamsimulator.jams.project.mips.MIPSFilesToAssemble;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.PlainTextChange;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class MIPSFileEditor extends CodeFileEditor {

	private final MIPSFileElements elements;

	private final Popup popup;
	private final VBox popupVBox;
	private final MIPSProject project;

	private final Subscription subscription;

	private final Object formattingLock = new Object();


	public MIPSFileEditor(FileEditorTab tab) {
		super(tab);

		popup = new Popup();
		popupVBox = new VBox();
		popupVBox.getStyleClass().add("mips-popup");
		popup.getContent().add(popupVBox);
		if (tab.getWorkingPane() instanceof MipsStructurePane) {
			project = ((MipsStructurePane) tab.getWorkingPane()).getProject();
			Optional<MIPSFileElements> elementsOptional = project.getData().getFilesToAssemble().getFileElements(tab.getFile());
			elements = elementsOptional.orElseGet(() -> new MIPSFileElements(project));
		} else {
			project = null;
			elements = new MIPSFileElements(null);
		}

		autocompletionPopup = new MIPSAutocompletionPopup(this);

		initializePopupListeners();
		applyLabelTabRemover();

		subscription = multiPlainChanges().subscribe(event -> event.forEach(this::index));
		index();
	}

	public Optional<MIPSProject> getProject() {
		return Optional.ofNullable(project);
	}

	public MIPSFileElements getElements() {
		return elements;
	}

	@Override
	public void reformat() {
		synchronized (formattingLock) {
			String reformattedCode = new MIPSCodeFormatter(elements).format();
			String text = getText();
			if (reformattedCode.equals(text)) return;
			int oLine = getCurrentParagraph();
			int oColumn = getCaretColumn();

			replaceText(0, text.length(), reformattedCode);

			List<CodeFileLine> lines = getLines();

			int newSize = lines.size();
			int line = Math.min(oLine, newSize - 1);
			int column = Math.min(oColumn, lines.get(line).getText().length());
			moveTo(line, column);


			double height = totalHeightEstimateProperty().getValue() == null ? 0 : totalHeightEstimateProperty().getValue();

			double toPixel = height * line / newSize - getLayoutBounds().getHeight() / 2;
			toPixel = Math.max(0, Math.min(height, toPixel));

			scrollPane.scrollYBy(toPixel);
			index(reformattedCode);
			tab.setSaveMark(true);
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		subscription.unsubscribe();
	}

	@Override
	public void reload() {
		super.reload();
		index();
	}

	@Override
	protected void applyAutoIndent() {
		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				int caretPosition = getCaretPosition();
				int currentLine = elements.lineOf(caretPosition);
				if (currentLine == -1) return;

				String previous = getLine(currentLine - 1).getText();

				MIPSLine line = elements.getLines().get(currentLine - 1);
				if (line.getLabel().isPresent()) {
					MIPSLabel label = line.getLabel().get();
					previous = previous.substring(label.getText().length());
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


	private void index(PlainTextChange change) {
		synchronized (formattingLock) {
			String added = change.getInserted();
			String removed = change.getRemoved();

			//Check current line.
			int currentLine = elements.lineOf(change.getPosition());
			if (currentLine == -1) {
				index();
				return;
			}

			boolean refresh = elements.editLine(currentLine, getLine(currentLine).getText());

			//Check next lines.
			int addedLines = StringUtils.charCount(added, '\n', '\r');
			int removedLines = StringUtils.charCount(removed, '\n', '\r');

			if (removedLines == 0 && addedLines == 0) {
				if (refresh && elements.getFilesToAssemble().isPresent()) {
					elements.getFilesToAssemble().ifPresent(MIPSFilesToAssemble::refreshGlobalLabels);
				} else {
					elements.update(this);
				}
				return;
			}

			currentLine++;
			int editedLines = Math.min(addedLines, removedLines);
			int linesToAdd = Math.max(0, addedLines - removedLines);
			int linesToRemove = Math.max(0, removedLines - addedLines);

			for (int i = 0; i < editedLines; i++) {
				refresh |= elements.editLine(currentLine + i, getLine(currentLine + i).getText());
			}

			if (linesToRemove > 0) {
				for (int i = 0; i < linesToRemove; i++) {
					refresh |= elements.removeLine(currentLine + editedLines);
				}
			} else if (linesToAdd > 0) {
				for (int i = 0; i < linesToAdd; i++) {
					refresh |= elements.addLine(currentLine + i + editedLines, getLine(currentLine + i + editedLines).getText());
				}
			}

			if (refresh && elements.getFilesToAssemble().isPresent()) {
				elements.getFilesToAssemble().ifPresent(MIPSFilesToAssemble::refreshGlobalLabels);
			} else {
				elements.update(this);
			}
		}
	}

	private void index() {
		elements.refreshAll(getText());
		elements.styleAll(this);
	}

	private void index(String text) {
		elements.refreshAll(text);
		elements.styleAll(this);
	}

	private void initializePopupListeners() {
		setMouseOverTextDelay(Duration.ofMillis(300));
		addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, event -> {
			int index = event.getCharacterIndex();
			Optional<MIPSCodeElement> optional = elements.getElementAt(index);
			if (!optional.isPresent()) return;
			popupVBox.getChildren().clear();
			optional.get().populatePopupWithErrors(popupVBox);
			Point2D position = event.getScreenPosition();
			popup.show(this, position.getX(), position.getY() + 10);
		});
		addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, event -> popup.hide());
	}

	private void applyLabelTabRemover() {
		addEventHandler(KeyEvent.KEY_TYPED, event -> {
			if (event.getCharacter().equals(":")) {
				int caretPosition = getCaretPosition();
				int currentParagraph = getCurrentParagraph();
				MIPSLine line = elements.getLines().get(currentParagraph);

				if (!line.getLabel().isPresent()) return;
				MIPSLabel label = line.getLabel().get();
				if (label.getEndIndex() != caretPosition - 1) return;

				String text = label.getText();

				int i = 0;
				for (char c : text.toCharArray()) {
					if (c != '\t' && c != ' ') break;
					i++;
				}
				if (i == 0) return;

				String first = text.substring(0, i);
				String last = text.substring(i);
				replaceText(label.getStartIndex(), label.getEndIndex() + 1, last + first);
			}
		});
	}
}
