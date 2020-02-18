package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;

public class FolderProjectFolderExplorer extends VBox {

    private File mainFolder;

    public FolderProjectFolderExplorer(File mainFolder) {
        this.mainFolder = mainFolder;


        addFiles(mainFolder, 0, 0, null);

    }

    public int indexOf(FolderExplorerFile file) {
        return getChildren().indexOf(file);
    }

    private int addFiles(File folder, int level, int index, FolderExplorerFolder parent) {
        File[] files = folder.listFiles();
        if (files == null) return index;


        FolderExplorerFolder newParent = new FolderExplorerFolder(this, level, null,
                new Label(folder.getName()), parent);
        getChildren().add(newParent);

        if(parent != null) {
            parent.getFiles().add(newParent);
        }

        level++;
        for (File file : files) {
            if (file.isDirectory()) index = addFiles(file, level, index, newParent);
            else {
                FolderExplorerFile folderExplorerFile = new FolderExplorerFile(this, level, null,
                        new Label(file.getName()), newParent);
                newParent.getFiles().add(folderExplorerFile);
                getChildren().add(folderExplorerFile);
            }
        }

        return index;
    }

}
