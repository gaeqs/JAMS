package net.jamsimulator.jams.gui.display.mips;

import javafx.geometry.Point2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.display.mips.element.MipsCodeElement;
import net.jamsimulator.jams.gui.display.mips.element.MipsFileElements;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedSet;

public class MipsFileDisplay extends FileDisplay {

	private final MipsFileElements elements;
	private final Popup popup;
	private final VBox popupVBox;

	public MipsFileDisplay(FileDisplayTab tab) {
		super(tab);
		elements = new MipsFileElements();

		popup = new Popup();
		popupVBox = new VBox();
		popupVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FF0000"), null, null)));
		popupVBox.getStyleClass().add("assembly-popup");
		popup.getContent().add(popupVBox);

		initializePopupListeners();

		Subscription subscription = multiPlainChanges().successionEnds(Duration.ofMillis(100))
				.subscribe(ignore -> index());
		index();
	}

	private void index() {
		elements.refresh(getText(), getTab().getWorkingPane());
		setStyleSpans(0, computeHighlighting());
	}

	private void initializePopupListeners() {
		setMouseOverTextDelay(Duration.ofSeconds(1));
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

	private StyleSpans<Collection<String>> computeHighlighting() {
		int textLength = getText().length();
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		if (textLength == 0) {
			spansBuilder.add(Collections.emptyList(), 0);
			return spansBuilder.create();
		}

		SortedSet<MipsCodeElement> codeElements = elements.getSortedElements();

		for (MipsCodeElement element : codeElements) {
			try {
				if (element.getStartIndex() != lastKwEnd) {
					spansBuilder.add(Collections.emptyList(), element.getStartIndex() - lastKwEnd);
				}

				spansBuilder.add(element.getStyles(), element.getEndIndex() - element.getStartIndex());

				lastKwEnd = element.getEndIndex();
			} catch (Exception exception) {
				System.out.println("Last: " + lastKwEnd);
				System.err.println("Element: " + element);
				throw exception;
			}
		}

		if (textLength > lastKwEnd) {
			spansBuilder.add(Collections.emptyList(), textLength - lastKwEnd);
		}
		return spansBuilder.create();
	}


}
