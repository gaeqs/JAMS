package net.jamsimulator.jams.gui.explorer.folder.context.file;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

public class FileMenuItemDelete extends LanguageMenuItem {

	public FileMenuItemDelete(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_DELETE);
		setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
		setOnAction(target -> contextMenu.getCurrentFile().getFile().delete());
	}

}
