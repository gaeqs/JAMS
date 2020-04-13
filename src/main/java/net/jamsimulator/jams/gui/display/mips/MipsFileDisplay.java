package net.jamsimulator.jams.gui.display.mips;

import javafx.geometry.Point2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.display.CodeFileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.display.mips.element.MipsCodeElement;
import net.jamsimulator.jams.gui.display.mips.element.MipsFileElements;
import net.jamsimulator.jams.gui.display.mips.element.MipsLine;
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

	public MipsFileDisplay(FileDisplayTab tab) {
		super(tab);
		elements = new MipsFileElements();

		popup = new Popup();
		popupVBox = new VBox();
		popupVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FF0000"), null, null)));
		popupVBox.getStyleClass().add("assembly-popup");
		popup.getContent().add(popupVBox);

		initializePopupListeners();

		subscription = multiPlainChanges().subscribe(event -> event.forEach(this::index));
		index();
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

		double toPixel = totalHeightEstimateProperty().getValue() * line / newSize - getLayoutBounds().getHeight() / 2;
		toPixel = Math.max(0, Math.min(scrollPane.getTotalHeightEstimate(), toPixel));

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
		addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, event -> {
			popup.hide();
		});
	}


}
