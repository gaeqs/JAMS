package net.jamsimulator.jams.gui.action.defaults.editortab;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.FileEditorTab;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.Messages;

public class EditorTabActionSplitHorizontally extends ContextAction {


	public static final String NAME = "EDITOR_TAB_SPLIT_HORIZONTALLY";
	public static final KeyCombination DEFAULT_COMBINATION = null;

	public EditorTabActionSplitHorizontally() {
		super(NAME, RegionTags.EDITOR_TAB, Messages.ACTION_EDITOR_TAB_SPLIT_HORIZONTALLY,
				DEFAULT_COMBINATION, EditorTagRegions.SPLIT, loadIcon());
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

	private static Image loadIcon() {
		return JamsApplication.getIconManager().getOrLoadSafe(Icons.TAB_SPLIT_HORIZONTALLY,
				Icons.TAB_SPLIT_HORIZONTALLY_PATH, 1024, 1024).orElse(null);
	}
}
