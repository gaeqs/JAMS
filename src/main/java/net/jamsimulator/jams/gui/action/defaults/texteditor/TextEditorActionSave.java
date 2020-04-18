package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.FileDisplay;

public class TextEditorActionSave extends Action {

	public static final String NAME = "TEXT_EDITOR_SAVE";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);

	public TextEditorActionSave() {
		super(NAME, RegionTags.TEXT_EDITOR, null, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Node node) {
		if (node instanceof FileDisplay) {
			((FileDisplay) node).save();
		}
	}
}
