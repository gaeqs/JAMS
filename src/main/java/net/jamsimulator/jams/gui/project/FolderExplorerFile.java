package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;


public class FolderExplorerFile extends HBox {

    protected FolderProjectFolderExplorer explorer;

    protected int hierarchyLevel;
    protected FolderExplorerFolder parent;

    protected Image image;
    protected Label name;

    public FolderExplorerFile(FolderProjectFolderExplorer explorer, int hierarchyLevel,
                              Image image, Label name, FolderExplorerFolder parent) {
        getStyleClass().add("folder-explorer-file");
        this.explorer = explorer;
        this.hierarchyLevel = hierarchyLevel;
        this.image = image;
        this.name = name;
        this.parent = parent;
        init();
    }

    public FolderProjectFolderExplorer getExplorer() {
        return explorer;
    }

    public boolean isParent(FolderExplorerFolder folder) {
        return parent == folder || parent != null && parent.isParent(folder);
    }

    public void remove() {
        explorer.getChildren().remove(this);
    }

    private void init() {
        Region region = new Region();
        region.setPrefWidth(hierarchyLevel * 20);
        getChildren().add(region);
        getChildren().add(new ImageView(image));
        getChildren().add(name);

    }
}
