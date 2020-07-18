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

package net.jamsimulator.jams.gui.editor;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.*;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.editor.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.theme.event.CodeFontChangeEvent;
import net.jamsimulator.jams.gui.theme.event.GeneralFontChangeEvent;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.KeyCombinationBuilder;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFileEditor extends CodeArea implements FileEditor, VirtualScrollHandled {

	protected final FileEditorTab tab;
	protected String old, original;
	protected VirtualizedScrollPane scrollPane;
	protected ScaledVirtualized zoom;

	protected AutocompletionPopup autocompletionPopup;
	private ChangeListener<? super Number> autocompletionMoveListener;

	public CodeFileEditor(FileEditorTab tab) {
		super(read(tab));
		this.tab = tab;
		this.original = getText();

		CustomLineNumberFactory factory = CustomLineNumberFactory.get(this);
		getChildren().add(0, factory.getBackground());

		JamsApplication.getThemeManager().getSelected().apply(this);
		JamsApplication.getThemeManager().registerListeners(this, true);

		setParagraphGraphicFactory(factory);
		applyOldTextListener();
		applyAutoIndent();
		applyIndentRemover();
		applySaveMarkListener();
		initializeAutocompletionPopupListeners();
		initializeActionsListeners();
		applyZoomListener();

		setOnContextMenuRequested(request -> {
			createContextMenu(request.getScreenX(), request.getScreenY());
			request.consume();
		});
	}

	public FileEditorTab getTab() {
		return tab;
	}

	public AutocompletionPopup getAutocompletionPopup() {
		return autocompletionPopup;
	}

	public CodeFileLine getLine(int index) {
		if (index < 0) return null;
		List<String> lines = StringUtils.multiSplit(getText(), "\n", "\r");

		int current = 0;
		int amount = 0;
		for (String line : lines) {
			if (current >= index) break;
			amount += line.length() + 1;

			current++;
		}

		return new CodeFileLine(index, lines.get(index), amount);
	}

	public CodeFileLine getLineFromAbsolutePosition(int position) {
		if (position < 0) return null;
		List<String> lines = StringUtils.multiSplit(getText(), "\n", "\r");

		int index = 0;
		int amount = 0;
		for (String line : lines) {
			position -= line.length() + 1;
			if (position < 0) break;
			amount += line.length() + 1;
			index++;
		}

		return new CodeFileLine(index, lines.get(index), amount);
	}

	public void reformat() {
	}

	public void duplicateCurrentLine() {
		CodeFileLine line = getLineFromAbsolutePosition(getCaretPosition());
		if (line == null) return;
		int end = line.getStart() + line.getText().length();
		replaceText(end, end, "\n" + line.getText());
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
	public boolean supportsActionRegion(String region) {
		return RegionTags.TEXT_EDITOR.equals(region) || RegionTags.EDITOR_TAB.equals(region);
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

	protected void initializeActionsListeners() {
		addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			try {
				KeyCodeCombination combination = new KeyCombinationBuilder(event).build();
				Runnable runnable = getScene().getAccelerators().get(combination);
				if (runnable != null) {
					runnable.run();


					KeyCode c = event.getCode();
					if (c == KeyCode.UP || c == KeyCode.DOWN || c == KeyCode.LEFT || c == KeyCode.RIGHT) return;
					event.consume();
				}
			} catch (IllegalArgumentException ignore) {
			}
		});
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> JamsApplication.hideContextMenu());
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
					if (current > 0.4) {
						zoom.getZoom().setX(current - 0.2);
						zoom.getZoom().setY(current - 0.2);
					}
				} else if (event.getDeltaY() > 0) {
					zoom.getZoom().setX(current + 0.2);
					zoom.getZoom().setY(current + 0.2);
				}
				event.consume();
			}
		});

		//RESET
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.isControlDown() && event.getButton() == MouseButton.MIDDLE) {
				zoom.getZoom().setX(1);
				zoom.getZoom().setY(1);
				zoom.getZoom().setZ(1);
			}
		});
	}

	private void createContextMenu(double screenX, double screenY) {
		Set<ContextAction> set = getSupportedContextActions();
		if (set.isEmpty()) return;
		ContextMenu main = new ContextActionMenuBuilder(this).addAll(set).build();
		JamsApplication.openContextMenu(main, this, screenX, screenY);
	}

	private Set<ContextAction> getSupportedContextActions() {
		Set<Action> actions = JamsApplication.getActionManager().getAll();
		Set<ContextAction> set = new HashSet<>();
		for (Action action : actions) {
			if (action instanceof ContextAction && supportsActionRegion(action.getRegionTag())
					&& ((ContextAction) action).supportsTextEditorState(this)) {
				set.add((ContextAction) action);
			}
		}
		return set;
	}

	private static String read(FileEditorTab tab) {
		try {
			return FileUtils.readAll(tab.getFile());
		} catch (IOException ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	@Listener
	private void onThemeChange(SelectedThemeChangeEvent.After event) {
		event.getNewTheme().apply(this);
	}

	@Listener
	private void onThemeChange(GeneralFontChangeEvent.After event) {
		JamsApplication.getThemeManager().getSelected().apply(this);
	}

	@Listener
	private void onThemeChange(CodeFontChangeEvent.After event) {
		JamsApplication.getThemeManager().getSelected().apply(this);
	}
}
