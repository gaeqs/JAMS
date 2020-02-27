package net.jamsimulator.jams.gui.explorer.context.file;

import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;

public class FileMenuItemRemove extends MenuItem {

    public FileMenuItemRemove(ExplorerFileDefaultContextMenu contextMenu) {
        super("Remove");
        setOnAction(target -> contextMenu.getCurrentFile().getFile().delete());
    }

}
