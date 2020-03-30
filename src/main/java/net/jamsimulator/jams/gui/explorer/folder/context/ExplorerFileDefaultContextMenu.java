package net.jamsimulator.jams.gui.explorer.folder.context;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerContextMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.context.option.*;
import net.jamsimulator.jams.gui.explorer.folder.context.option.newmenu.FileMenuItemNew;

import java.awt.*;

public class ExplorerFileDefaultContextMenu extends ContextMenu {

	public static final ExplorerFileDefaultContextMenu INSTANCE = new ExplorerFileDefaultContextMenu();

	private ExplorerElement element;

	private ExplorerFileDefaultContextMenu() {

		getItems().add(new FileMenuItemNew(this));

		getItems().add(new SeparatorMenuItem());

		getItems().add(new FileMenuItemCopy(this));
		getItems().add(new FileMenuItemPaste(this));

		getItems().add(new SeparatorMenuItem());

		getItems().add(new FileMenuItemDelete(this));

		getItems().add(new SeparatorMenuItem());

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			getItems().add(new FileMenuItemShowInFiles(this));
		}
	}

	public ExplorerElement getCurrentElement() {
		return element;
	}

	public void setCurrentExplorerElement(ExplorerElement element) {
		this.element = element;
		getItems().forEach(target -> {
			if (target instanceof ExplorerContextMenuItem)
				((ExplorerContextMenuItem) target).onElementChange(element);
		});
	}

}
