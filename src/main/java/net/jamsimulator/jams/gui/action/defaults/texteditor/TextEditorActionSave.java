package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.FileDisplay;

public class TextEditorActionSave extends Action {

	public static final String NAME = "EDITOR_SAVE";

	public TextEditorActionSave() {
		super(NAME, RegionTags.TEXT_EDITOR, null);
	}

	@Override
	public void run(Node node) {
		if (node instanceof FileDisplay) {
			((FileDisplay) node).save();
		}
	}
}
