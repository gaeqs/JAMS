package net.jamsimulator.jams.gui.explorer.context.file;

import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;

import java.awt.*;
import java.io.IOException;

public class FileMenuItemShowInFiles extends MenuItem {

    public FileMenuItemShowInFiles(ExplorerFileDefaultContextMenu contextMenu) {
        super("Show in files");
        setOnAction(target -> new Thread(() -> {
            try {
                Desktop.getDesktop().browse(contextMenu.getCurrentFile().getParentFolder().getFolder().toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());
    }

}
