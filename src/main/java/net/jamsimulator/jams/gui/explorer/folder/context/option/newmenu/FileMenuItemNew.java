package net.jamsimulator.jams.gui.explorer.folder.context.option.newmenu;

import net.jamsimulator.jams.gui.explorer.ExplorerContextMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;

public class FileMenuItemNew extends LanguageMenu implements ExplorerContextMenuItem {

	public FileMenuItemNew(ExplorerFileDefaultContextMenu menu) {
		super(Messages.EXPLORER_ITEM_ACTION_NEW);
		getItems().add(new FileMenuItemNewAssemblyFile(menu));
		getItems().add(new FileMenuItemNewFolder(menu));
		getItems().add(new FileMenuItemNewFile(menu));
	}

	@Override
	public void onElementChange(ExplorerElement element) {
		setVisible(element instanceof ExplorerFile || element instanceof ExplorerFolder);
		getItems().forEach(target -> {
			if (target instanceof ExplorerContextMenuItem)
				((ExplorerContextMenuItem) target).onElementChange(element);
		});
	}

}
