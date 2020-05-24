package net.jamsimulator.jams.gui.action.defaults.editortab;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.FileEditorTab;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.language.Messages;

public class EditorTabActionSplitHorizontally extends ContextAction {


	public static final String NAME = "EDITOR_TAB_SPLIT_HORIZONTALLY";
	public static final KeyCombination DEFAULT_COMBINATION = null;

	public EditorTabActionSplitHorizontally() {
		super(NAME, RegionTags.EDITOR_TAB, Messages.ACTION_EDITOR_TAB_SPLIT_HORIZONTALLY,
				DEFAULT_COMBINATION, EditorTagRegions.SPLIT, null);
	}

	@Override
	public void run(Object node) {
		FileEditorTab tab = null;
		if (node instanceof FileEditorTab)
			tab = (FileEditorTab) node;
		else if (node instanceof FileEditor)
			tab = ((FileEditor) node).getTab();
		if (tab == null) return;

		tab.openInNewHolder(true);
	}

	@Override
	public boolean supportsExplorerState(Explorer explorer) {
		return false;
	}
}
