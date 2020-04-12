package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.CodeFileDisplay;

public class TextEditorActionReformat extends Action {

	public static final String NAME = "TEXT_EDITOR_REFORMAT";

	public TextEditorActionReformat() {
		super(NAME, RegionTags.TEXT_EDITOR);
	}

	@Override
	public void run(Node node) {
		if (node instanceof CodeFileDisplay) {
			((CodeFileDisplay) node).reformat();
		}
	}
}
