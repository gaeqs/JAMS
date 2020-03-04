package net.jamsimulator.jams.gui.explorer.folder.context.file.newmenu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.general.NewFolderWindow;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

public class FileMenuItemNewFolder extends LanguageMenuItem {

	public FileMenuItemNewFolder(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_NEW_FOLDER);
		initIcon();
		setOnAction(event -> NewFolderWindow.open(contextMenu.getCurrentFile().getParentSection().getFolder()));
	}

	private void initIcon() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.FILE_FOLDER, Icons.FILE_FOLDER_PATH,
				FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE).orElse(null);

		if (icon != null) {
			setGraphic(new ImageView(icon));
		}
	}

}
