package net.jamsimulator.jams.gui.explorer.context.file.newmenu;

import javafx.scene.control.Menu;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;

public class FileMenuItemNew extends Menu {

	public FileMenuItemNew(ExplorerFileDefaultContextMenu menu) {
		super("New");
		getItems().add(new FileMenuItemNewAssemblyFile(menu));
		getItems().add(new FileMenuItemNewFile(menu));
	}

}
