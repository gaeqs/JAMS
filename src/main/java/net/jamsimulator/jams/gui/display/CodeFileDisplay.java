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

package net.jamsimulator.jams.gui.display;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.input.*;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.TaggedRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;
import net.jamsimulator.jams.utils.FileUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFileDisplay extends CodeArea implements FileDisplay, TaggedRegion, VirtualScrollHandled {

	protected final FileDisplayTab tab;
	protected String old, original;
	protected VirtualizedScrollPane scrollPane;
	protected ScaledVirtualized zoom;

	protected AutocompletionPopup autocompletionPopup;
	private ChangeListener<? super Number> autocompletionMoveListener;

	public CodeFileDisplay(FileDisplayTab tab) {
		super(read(tab));
		this.tab = tab;
		this.original = getText();

		CustomLineNumberFactory factory = CustomLineNumberFactory.get(this);
		getChildren().add(0, factory.getBackground());

		JamsApplication.getThemeManager().getSelected().apply(this);
		JamsApplication.getThemeManager().registerListeners(this);

		setParagraphGraphicFactory(factory);
		applyOldTextListener();
		applyAutoIndent();
		applyIndentRemover();
		applySaveMarkListener();
		initializeAutocompletionPopupListeners();
		applyZoomListener();
	}

	public FileDisplayTab getTab() {
		return tab;
	}

	public AutocompletionPopup getAutocompletionPopup() {
		return autocompletionPopup;
	}

	public void reformat() {
	}

	public void onClose() {
		JamsApplication.getThemeManager().unregisterListeners(this);
		JamsApplication.getStage().xProperty().removeListener(autocompletionMoveListener);
		JamsApplication.getStage().yProperty().removeListener(autocompletionMoveListener);
		JamsApplication.getStage().widthProperty().removeListener(autocompletionMoveListener);
		JamsApplication.getStage().heightProperty().removeListener(autocompletionMoveListener);
	}

	@Override
	public void save() {
		try {
			FileUtils.writeAll(tab.getFile(), original = getText());
			tab.setSaveMark(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reload() {
		replaceText(0, getText().length(), original = read(tab));
		tab.setSaveMark(false);
	}

	@Override
	public String getTag() {
		return RegionTags.TEXT_EDITOR;
	}

	@Override
	public VirtualizedScrollPane getScrollPane() {
		return scrollPane;
	}

	@Override
	public void setScrollPane(VirtualizedScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	@Override
	public ScaledVirtualized getZoom() {
		return zoom;
	}

	@Override
	public void setZoom(ScaledVirtualized zoom) {
		this.zoom = zoom;
	}

	private void applyOldTextListener() {
		textProperty().addListener((obs, old, value) -> this.old = old);
	}

	protected void applyAutoIndent() {
		Pattern whiteSpace = Pattern.compile("^\\s+");
		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				int caretPosition = getCaretPosition();
				int currentParagraph = getCurrentParagraph();
				Matcher m0 = whiteSpace.matcher(getParagraph(currentParagraph - 1).getSegments().get(0));
				if (m0.find()) Platform.runLater(() -> insertText(caretPosition, m0.group()));
			}
		});
	}

	protected void applyIndentRemover() {
		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.BACK_SPACE) {

				int caretPosition = getCaretPosition();

				//If shift is pressed, then just execute a normal backspace.
				if (event.isShiftDown()) {
					return;
				}

				int currentParagraph = getCurrentParagraph();
				Position position = offsetToPosition(caretPosition, Bias.Forward);

				String s = old.substring(caretPosition - position.getMinor(), caretPosition + 1);
				if (s.trim().isEmpty()) {
					int to = caretPosition - position.getMinor() - 1;
					if (to < 0) to = 0;

					boolean lastParagraphEmpty = currentParagraph != 0 && getParagraph(currentParagraph - 1).getText().isEmpty();

					replaceText(to, caretPosition, lastParagraphEmpty ? s : "");
				}
			}
		});
	}

	protected void initializeAutocompletionPopupListeners() {
		//AUTO COMPLETION
		addEventHandler(KeyEvent.KEY_TYPED, event -> {
			if (autocompletionPopup == null) return;
			autocompletionPopup.managePressEvent(event);
		});

		//AUTOCOMPLETION MOVEMENT
		addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (autocompletionPopup == null) return;
			if (autocompletionPopup.manageTypeEvent(event)) event.consume();
		});

		//FOCUS
		focusedProperty().addListener((obs, old, val) -> {
			if (autocompletionPopup == null) return;
			autocompletionPopup.hide();
		});
		//CLICK
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (autocompletionPopup == null) return;
			autocompletionPopup.hide();
		});

		//MOVE
		autocompletionMoveListener = (obs, old, val) -> {
			if (autocompletionPopup == null) return;
			autocompletionPopup.hide();
		};
		JamsApplication.getStage().xProperty().addListener(autocompletionMoveListener);
		JamsApplication.getStage().yProperty().addListener(autocompletionMoveListener);
		JamsApplication.getStage().widthProperty().addListener(autocompletionMoveListener);
		JamsApplication.getStage().heightProperty().addListener(autocompletionMoveListener);
	}

	protected void applySaveMarkListener() {
		addEventHandler(KeyEvent.KEY_TYPED, event -> {
			if (event.getCharacter().isEmpty()) return;
			tab.setSaveMark(!getText().equals(original));
		});
	}

	protected void applyZoomListener() {
		addEventFilter(ScrollEvent.SCROLL, event -> {
			if (event.isControlDown()) {
				double current = zoom.getZoom().getX();
				if (event.getDeltaY() < 0) {
					if (current <= 0.4) return;
					zoom.getZoom().setX(current - 0.2);
					zoom.getZoom().setY(current - 0.2);
				} else {
					zoom.getZoom().setX(current + 0.2);
					zoom.getZoom().setY(current + 0.2);
				}
				event.consume();
			}
		});

		//RESET
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
 			if(event.isControlDown() && event.getButton() == MouseButton.MIDDLE) {
				zoom.getZoom().setX(1);
				zoom.getZoom().setY(1);
				zoom.getZoom().setZ(1);
			}
		});
	}

	private static String read(FileDisplayTab tab) {
		try {
			return FileUtils.readAll(tab.getFile());
		} catch (IOException ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	@Listener
	public void onThemeChange(SelectedThemeChangeEvent.After event) {
		event.getNewTheme().apply(this);
	}
}
