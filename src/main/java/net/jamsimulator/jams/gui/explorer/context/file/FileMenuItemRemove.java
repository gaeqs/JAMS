package net.jamsimulator.jams.gui.explorer.context.file;

import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerFile;

public class FileMenuItemRemove extends MenuItem {

	public FileMenuItemRemove(ExplorerFile file) {
		super("Remove");
		setOnAction(target -> file.getFile().delete());
	}

}
