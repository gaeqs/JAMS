package net.jamsimulator.jams.gui.explorer.folder.context.option;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import net.jamsimulator.jams.gui.explorer.ExplorerContextMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;

public class FileMenuItemDelete extends LanguageMenuItem implements ExplorerContextMenuItem {

	public FileMenuItemDelete(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_DELETE);
		setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
		setOnAction(target -> run(contextMenu));
	}

	@Override
	public void onElementChange(ExplorerElement element) {
		setVisible(element instanceof ExplorerFile || element instanceof ExplorerFolder);
	}

	private void run(ExplorerFileDefaultContextMenu contextMenu) {
		ExplorerElement element = contextMenu.getCurrentElement();
		File file;

		if (element instanceof ExplorerFile) {
			file = ((ExplorerFile) element).getFile();
		} else if (element instanceof ExplorerFolder) {
			file = ((ExplorerFolder) element).getFolder();
		} else {
			throw new IllegalStateException("Element is not a file or a folder!");
		}

		FileUtils.deleteDirectory(file);
	}

}
