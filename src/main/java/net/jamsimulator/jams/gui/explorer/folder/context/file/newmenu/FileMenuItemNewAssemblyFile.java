package net.jamsimulator.jams.gui.explorer.folder.context.file.newmenu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.general.NewAssemblyFileWindow;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

public class FileMenuItemNewAssemblyFile extends LanguageMenuItem {

	public FileMenuItemNewAssemblyFile(ExplorerFileDefaultContextMenu contextMenu) {
		super(Messages.EXPLORER_ITEM_ACTION_NEW_ASSEMBLY_FILE);
		initIcon();
		setOnAction(event -> NewAssemblyFileWindow.open(contextMenu.getCurrentFile().getParentSection().getFolder()));
	}

	private void initIcon() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.FILE_ASSEMBLY, Icons.FILE_ASSEMBLY_PATH,
				FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE).orElse(null);

		if (icon != null) {
			setGraphic(new ImageView(icon));
		}
	}
}
