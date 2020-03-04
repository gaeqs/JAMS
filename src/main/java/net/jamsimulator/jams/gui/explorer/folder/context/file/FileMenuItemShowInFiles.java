package net.jamsimulator.jams.gui.explorer.folder.context.file;

import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;

import java.awt.*;
import java.io.IOException;

public class FileMenuItemShowInFiles extends LanguageMenuItem {

    public FileMenuItemShowInFiles(ExplorerFileDefaultContextMenu contextMenu) {
        super(Messages.EXPLORER_ITEM_ACTION_SHOW_IN_FILES);
        setOnAction(target -> new Thread(() -> {
            try {
                Desktop.getDesktop().browse(contextMenu.getCurrentFile().getParentSection().getFolder().toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());
    }

}
