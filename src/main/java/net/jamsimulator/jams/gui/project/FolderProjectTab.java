package net.jamsimulator.jams.gui.project;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.project.FolderProject;
import net.jamsimulator.jams.utils.AnchorUtils;

public class FolderProjectTab extends Tab {

	private FolderProject project;
	private TabPane projectTabPane;

	public FolderProjectTab(FolderProject project) {
		super(project.getName());
		this.project = project;

		AnchorPane pane = new AnchorPane();

		//Black line separator
		Separator separator = new Separator(Orientation.HORIZONTAL);
		AnchorUtils.setAnchor(separator, 0, -1, 0, 0);
		pane.getChildren().add(separator);

		projectTabPane = new TabPane();
		AnchorUtils.setAnchor(projectTabPane, 1, 0, 0, 0);
		pane.getChildren().add(projectTabPane);

		Tab tab = new Tab("Project Structure");
		tab.setClosable(false);
		projectTabPane.getTabs().add(tab);

		WorkingPane structurePane = new WorkingPane(this, new TextArea());
		tab.setContent(structurePane);

		setContent(pane);
	}

	public FolderProject getProject() {
		return project;
	}

	public TabPane getProjectTabPane() {
		return projectTabPane;
	}
}
