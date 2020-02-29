package net.jamsimulator.jams.gui.explorer.context.file.newmenu;

import javafx.scene.control.Menu;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;

public class FileMenuItemNew extends LanguageMenu {

	public FileMenuItemNew(ExplorerFileDefaultContextMenu menu) {
		super(Messages.EXPLORER_ITEM_ACTION_NEW);
		getItems().add(new FileMenuItemNewAssemblyFile(menu));
		getItems().add(new FileMenuItemNewFile(menu));
	}

}
