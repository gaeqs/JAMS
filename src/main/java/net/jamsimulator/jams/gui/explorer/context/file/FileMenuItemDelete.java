package net.jamsimulator.jams.gui.explorer.context.file;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;

public class FileMenuItemDelete extends MenuItem {

	public FileMenuItemDelete(ExplorerFileDefaultContextMenu contextMenu) {
		super("Delete");
		setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
		setOnAction(target -> contextMenu.getCurrentFile().getFile().delete());
	}

}
