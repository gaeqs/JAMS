package net.jamsimulator.jams.gui.explorer.folder.context.option;

import net.jamsimulator.jams.gui.explorer.ExplorerContextMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileMenuItemShowInFiles extends LanguageMenuItem implements ExplorerContextMenuItem {

	public FileMenuItemShowInFiles(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_SHOW_IN_FILES);
		setOnAction(target -> run(contextMenu));
	}

	@Override
	public void onElementChange(ExplorerElement element) {
		setVisible(element instanceof ExplorerFile || element instanceof ExplorerFolder);
	}

	private void run(ExplorerFileDefaultContextMenu contextMenu) {
		ExplorerElement element = contextMenu.getCurrentElement();
		File folder;

		if (element instanceof ExplorerFile) {
			folder = ((ExplorerFile) element).getParentSection().getFolder();
		} else if (element instanceof ExplorerFolder) {
			ExplorerSection section = ((ExplorerFolder) element).getParentSection();
			if (section instanceof ExplorerFolder) {
				folder = ((ExplorerFolder) section).getFolder();
			} else {
				folder = ((ExplorerFolder) element).getFolder();
			}
		} else {
			throw new IllegalStateException("Element is not a file or a folder!");
		}

		new Thread(() -> {
			try {
				Desktop.getDesktop().browse(folder.toURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
