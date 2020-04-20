package net.jamsimulator.jams.gui.mips.display;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.display.CodeFileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.mips.display.element.DisplayLabel;
import net.jamsimulator.jams.gui.mips.display.element.MipsCodeElement;
import net.jamsimulator.jams.gui.mips.display.element.MipsFileElements;
import net.jamsimulator.jams.gui.mips.display.element.MipsLine;
import net.jamsimulator.jams.gui.mips.project.MipsProjectPane;
import net.jamsimulator.jams.project.MipsProject;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.PlainTextChange;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class MipsFileDisplay extends CodeFileDisplay {

	private final MipsFileElements elements;

	private final Popup popup;
	private final VBox popupVBox;
	private final Subscription subscription;
	private final MipsProject project;

	public MipsFileDisplay(FileDisplayTab tab) {
		super(tab);
		elements = new MipsFileElements();

		popup = new Popup();
		popupVBox = new VBox();
		popupVBox.getStyleClass().add("mips-popup");
		popup.getContent().add(popupVBox);

		if (tab.getWorkingPane() instanceof MipsProjectPane) {
			project = ((MipsProjectPane) tab.getWorkingPane()).getProject();
		} else {
			project = null;
		}

		autocompletionPopup = new MipsAutocompletionPopup(this);

		initializePopupListeners();
		applyLabelTabRemover();

		subscription = multiPlainChanges().subscribe(event -> event.forEach(this::index));
		index();
	}

	public Optional<MipsProject> getProject() {
		return Optional.ofNullable(project);
	}

	public MipsFileElements getElements() {
		return elements;
	}

	@Override
	public void reformat() {
		String reformattedCode = elements.getReformattedCode();
		String text = getText();
		if (reformattedCode.equals(text)) return;
		int line = getCurrentParagraph();
		int column = getCaretColumn();

		replaceText(0, text.length(), reformattedCode);

		int newSize = getParagraphs().size();
		line = Math.min(line, newSize - 1);
		column = Math.min(column, getParagraphLength(line));
		moveTo(line, column);


		double height = totalHeightEstimateProperty().getValue() == null ? 0 : totalHeightEstimateProperty().getValue();

		double toPixel = height * line / newSize - getLayoutBounds().getHeight() / 2;
		toPixel = Math.max(0, Math.min(height, toPixel));

		scrollPane.scrollYBy(toPixel);
	}

	@Override
	public void onClose() {
		super.onClose();
		subscription.unsubscribe();
	}

	private void index(PlainTextChange change) {
		String added = change.getInserted();
		String removed = change.getRemoved();

		//Check current line.
		int currentLine = elements.lineOf(change.getPosition());
		if (currentLine == -1) {
			index();
			return;
		}
		elements.editLine(currentLine, getParagraph(currentLine).getText());

		//Check next lines.
		int addedLines = StringUtils.charCount(added, '\n', '\r');
		int removedLines = StringUtils.charCount(removed, '\n', '\r');

		if (removedLines == 0 && addedLines == 0) {
			elements.searchAllErrors(getTab().getWorkingPane(), currentLine, 1);
			elements.styleLines(this, elements.searchLabelErrors());
			elements.styleLines(this, currentLine, 1);
			return;
		}

		currentLine++;
		int editedLines = Math.min(addedLines, removedLines);
		int linesToAdd = Math.max(0, addedLines - removedLines);
		int linesToRemove = Math.max(0, removedLines - addedLines);

		for (int i = 0; i < editedLines; i++) {
			elements.editLine(currentLine + i, getParagraph(currentLine + i).getText());
		}

		if (linesToRemove > 0) {
			for (int i = 0; i < linesToRemove; i++) {
				elements.removeLine(currentLine + editedLines);
			}
		} else if (linesToAdd > 0) {
			for (int i = 0; i < linesToAdd; i++) {
				elements.addLine(currentLine + i + editedLines,
						getParagraph(currentLine + i + editedLines).getText());
			}
		}

		elements.searchAllErrors(getTab().getWorkingPane(), currentLine - 1, 1 + editedLines + linesToAdd);
		elements.styleLines(this, elements.searchLabelErrors());
		elements.styleLines(this, currentLine - 1, 1 + editedLines + linesToAdd);
	}

	private void index() {
		elements.refreshAll(getText(), getTab().getWorkingPane());
		List<MipsLine> lines = elements.getLines();
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).styleLine(this, i);
		}
	}

	private void initializePopupListeners() {
		setMouseOverTextDelay(Duration.ofMillis(300));
		addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, event -> {
			int index = event.getCharacterIndex();
			Optional<MipsCodeElement> optional = elements.getElementAt(index);
			if (!optional.isPresent()) return;
			popupVBox.getChildren().clear();
			optional.get().populatePopup(popupVBox);
			Point2D position = event.getScreenPosition();
			popup.show(this, position.getX(), position.getY() + 10);
		});
		addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, event -> popup.hide());
	}

	@Override
	protected void applyAutoIndent() {
		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				int caretPosition = getCaretPosition();
				int currentParagraph = getCurrentParagraph();

				String previous = getParagraph(currentParagraph - 1).getSegments().get(0);

				MipsLine line = elements.getLines().get(currentParagraph - 1);
				if (line.getLabel().isPresent()) {
					DisplayLabel label = line.getLabel().get();
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

	private void applyLabelTabRemover() {
		addEventHandler(KeyEvent.KEY_TYPED, event -> {
			if (event.getCharacter().equals(":")) {
				int caretPosition = getCaretPosition();
				int currentParagraph = getCurrentParagraph();
				MipsLine line = elements.getLines().get(currentParagraph);

				if (!line.getLabel().isPresent()) return;
				DisplayLabel label = line.getLabel().get();
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
