package net.jamsimulator.jams.gui.explorer.folder.context.option.newmenu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.ExplorerContextMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.general.NewAssemblyFileWindow;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

import java.io.File;

public class FileMenuItemNewAssemblyFile extends LanguageMenuItem implements ExplorerContextMenuItem {

	public FileMenuItemNewAssemblyFile(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_NEW_ASSEMBLY_FILE);
		initIcon();
		setOnAction(event -> run(contextMenu));
	}

	private void initIcon() {
		JamsApplication.getIconManager().getOrLoadSafe(Icons.FILE_ASSEMBLY, Icons.FILE_ASSEMBLY_PATH,
				FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE).ifPresent(icon -> setGraphic(new ImageView(icon)));

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
			folder = ((ExplorerFolder) element).getFolder();
		} else {
			throw new IllegalStateException("Element is not a file or a folder!");
		}

		NewAssemblyFileWindow.open(folder);
	}
}
