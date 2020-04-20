package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayList;

public class TextEditorActionNextFile extends Action {

	public static final String NAME = "TEXT_EDITOR_NEXT_FILE";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

	public TextEditorActionNextFile() {
		super(NAME, RegionTags.TEXT_EDITOR, null, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Node node) {
		if (node instanceof FileDisplay) {
			FileDisplayList list = ((FileDisplay) node).getTab().getList();
			list.selectNext();
			list.getSelected().ifPresent(target -> {
				if (target.getDisplay() instanceof Node) {
					((Node) target.getDisplay()).requestFocus();
				}
			});
		}
	}
}
