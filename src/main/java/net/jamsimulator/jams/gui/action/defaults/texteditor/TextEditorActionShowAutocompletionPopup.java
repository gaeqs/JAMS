package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.CodeFileDisplay;
import net.jamsimulator.jams.gui.display.popup.AutocompletionPopup;

public class TextEditorActionShowAutocompletionPopup extends Action {

	public static final String NAME = "TEXT_EDITOR_SHOW_AUTOCOMPLETION_POPUP";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.SHORTCUT_DOWN);

	public TextEditorActionShowAutocompletionPopup() {
		super(NAME, RegionTags.TEXT_EDITOR, null, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Node node) {
		if (node instanceof CodeFileDisplay) {
			AutocompletionPopup popup = ((CodeFileDisplay) node).getAutocompletionPopup();
			popup.execute(0, true);
		}
	}
}
