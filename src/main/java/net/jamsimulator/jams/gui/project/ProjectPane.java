package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.sidebar.SidebarButton;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.Project;

/**
 * This class represent the working pane of a project.
 */
public class ProjectPane extends WorkingPane {

	private Project project;
	private FolderExplorer explorer;

	public ProjectPane(Tab parent, ProjectTab projectTab, Project project) {
		super(parent, projectTab, new TextArea());
		this.project = project;
		loadSidebarModules();
	}


	private void loadSidebarModules() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, SidebarButton.IMAGE_SIZE, SidebarButton.IMAGE_SIZE).orElse(null);
		explorer = new FolderExplorer(project.getFolder());
		topLeftSidebar.addNode("Explorer", explorer, explorerIcon, Messages.EXPLORER_NAME);
	}

	@Override
	protected void onClose() {
		explorer.killWatchers();
	}
}
