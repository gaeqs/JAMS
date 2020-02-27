package net.jamsimulator.jams.gui.explorer.context;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.context.file.FileMenuItemRemove;
import net.jamsimulator.jams.gui.explorer.context.file.FileMenuItemShowInFiles;

import java.awt.*;

public class ExplorerFileDefaultContextMenu extends ContextMenu {

	private ExplorerFile file;

	public ExplorerFileDefaultContextMenu(ExplorerFile file) {
		this.file = file;

		getItems().add(new FileMenuItemRemove(file));

		getItems().add(new SeparatorMenuItem());

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			getItems().add(new FileMenuItemShowInFiles(file));
		}
	}

}
