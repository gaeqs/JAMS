package net.jamsimulator.jams.gui.explorer.context.file.newmenu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.general.NewFileWindow;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

public class FileMenuItemNewFile extends LanguageMenuItem {

	public FileMenuItemNewFile(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_NEW_FILE);
		initIcon();
		setOnAction(event -> NewFileWindow.open(contextMenu.getCurrentFile().getParentFolder().getFolder()));
	}

	private void initIcon() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.FILE_FILE, Icons.FILE_FILE_PATH,
				FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE).orElse(null);

		if (icon != null) {
			setGraphic(new ImageView(icon));
		}
	}

}
