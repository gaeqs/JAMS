package net.jamsimulator.jams.gui.explorer.context;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.context.file.FileMenuItemRemove;
import net.jamsimulator.jams.gui.explorer.context.file.FileMenuItemShowInFiles;
import net.jamsimulator.jams.gui.explorer.context.file.newmenu.FileMenuItemNew;

import java.awt.*;

public class ExplorerFileDefaultContextMenu extends ContextMenu {

	public static final ExplorerFileDefaultContextMenu INSTANCE = new ExplorerFileDefaultContextMenu();

	private ExplorerFile file;

	private ExplorerFileDefaultContextMenu() {

		getItems().add(new FileMenuItemNew(this));

		getItems().add(new SeparatorMenuItem());

		getItems().add(new FileMenuItemRemove(this));

		getItems().add(new SeparatorMenuItem());

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			getItems().add(new FileMenuItemShowInFiles(this));
		}
	}

	public ExplorerFile getCurrentFile() {
		return file;
	}

	public void setCurrentExplorerFile(ExplorerFile file) {
		this.file = file;
	}

}
