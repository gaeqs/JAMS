package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.sidebar.SidebarButton;
import net.jamsimulator.jams.project.Project;

/**
 * This class represent the working pane of a project.
 */
public class ProjectPane extends WorkingPane {

	private Project project;
	private ExplorerPane explorer;

	public ProjectPane(Tab parent, Project project) {
		super(parent, new TextArea());
		this.project = project;
		loadSidebarModules();
	}


	private void loadSidebarModules() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, SidebarButton.IMAGE_SIZE, SidebarButton.IMAGE_SIZE).orElse(null);
		explorer = new ExplorerPane(project.getFolder());
		topLeftSidebar.addNode("Explorer", explorer, explorerIcon);

		JamsApplication.getStage().onCloseRequestProperty().addListener(event -> {
			System.out.println("TEST");
			explorer.kill();
		});
	}
}
