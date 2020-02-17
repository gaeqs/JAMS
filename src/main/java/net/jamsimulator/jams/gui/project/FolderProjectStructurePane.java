package net.jamsimulator.jams.gui.project;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import net.jamsimulator.jams.project.FolderProject;

public class FolderProjectStructurePane extends SplitPane {

	private FolderProjectTab folderProjectTab;

	public FolderProjectStructurePane(FolderProjectTab folderProjectTab) {
		this.folderProjectTab = folderProjectTab;

		FolderProjectFolderExplorer explorer = new FolderProjectFolderExplorer(getProject().getFolder());
		ScrollPane scrollPane = new ScrollPane(explorer);
		getItems().add(scrollPane);

		TextArea area = new TextArea();
		getItems().add(area);


	}

	public FolderProjectTab getFolderProjectTab() {
		return folderProjectTab;
	}

	public FolderProject getProject() {
		return folderProjectTab.getProject();
	}
}
