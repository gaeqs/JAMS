package net.jamsimulator.jams.gui.project;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.project.FolderProject;

public class FolderProjectStructurePane extends SplitPane {

	private FolderProjectTab folderProjectTab;

	public FolderProjectStructurePane(FolderProjectTab folderProjectTab) {
		this.folderProjectTab = folderProjectTab;

		AnchorPane first = new AnchorPane();
		getItems().add(first);

		TextArea area = new TextArea();
		getItems().add(area);


	}

	public FolderProjectTab getFolderProjectTab() {
		return folderProjectTab;
	}

	public FolderProject getProject () {
		return folderProjectTab.getProject();
	}
}
