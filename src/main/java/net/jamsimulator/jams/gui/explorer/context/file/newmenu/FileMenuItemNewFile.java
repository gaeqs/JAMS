package net.jamsimulator.jams.gui.explorer.context.file.newmenu;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.general.NewFileWindow;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;

public class FileMenuItemNewFile extends MenuItem {

	public FileMenuItemNewFile(ExplorerFileDefaultContextMenu contextMenu) {
		super("File");
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
