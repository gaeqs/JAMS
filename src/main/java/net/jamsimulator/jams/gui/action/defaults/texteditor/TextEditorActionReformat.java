package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.CodeFileDisplay;

public class TextEditorActionReformat extends Action {

	public static final String NAME = "TEXT_EDITOR_REFORMAT";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN);

	public TextEditorActionReformat() {
		super(NAME, RegionTags.TEXT_EDITOR, null, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Node node) {
		if (node instanceof CodeFileDisplay) {
			((CodeFileDisplay) node).reformat();
		}
	}
}
