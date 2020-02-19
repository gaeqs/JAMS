package net.jamsimulator.jams.gui.project;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.Sidebar;
import net.jamsimulator.jams.project.FolderProject;
import net.jamsimulator.jams.utils.AnchorUtils;

public class FolderProjectStructurePane extends AnchorPane {

	public static final int SIDEBAR_WIDTH = 22;

	private FolderProjectTab folderProjectTab;
	private SplitPane centerSplitPane;

	private FolderProjectFolderExplorer explorer;
	private ScrollPane explorerScrollPane;
	private double explorerWidth ;

	public FolderProjectStructurePane(FolderProjectTab folderProjectTab) {
		this.folderProjectTab = folderProjectTab;

		//Split pane.
		centerSplitPane = new SplitPane();
		getChildren().add(centerSplitPane);
		AnchorUtils.setAnchor(centerSplitPane, 0, 0, SIDEBAR_WIDTH, 0);

		//Folder explorer.
		explorer = new FolderProjectFolderExplorer(getProject().getFolder());
		explorerScrollPane = new ScrollPane(explorer);
		explorerWidth = 0.2;

		//Text area
		TextArea area = new TextArea();
		area.setFont(new Font(FontLoader.JETBRAINS_MONO, 15));
		centerSplitPane.getItems().add(area);
		generateLeftSidebar();
	}


	public FolderProjectTab getFolderProjectTab() {
		return folderProjectTab;
	}

	public FolderProject getProject() {
		return folderProjectTab.getProject();
	}

	public SplitPane getCenterSplitPane() {
		return centerSplitPane;
	}

	public FolderProjectFolderExplorer getExplorer() {
		return explorer;
	}

	public ScrollPane getExplorerScrollPane() {
		return explorerScrollPane;
	}

	private void generateLeftSidebar() {
		SidePane pane = new SidePane();
		//TODO
		Sidebar sidebar = new Sidebar(pane, true, true);
		AnchorUtils.setAnchor(sidebar, 0, 0, 0, -1);
		sidebar.setPrefWidth(SIDEBAR_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_WIDTH);
		getChildren().add(sidebar);

		sidebar.createSidebarButton("Project", (obs, old, val) -> {
			if (val) {
				centerSplitPane.getItems().add(0, explorerScrollPane);
				centerSplitPane.setDividerPosition(0, explorerWidth);
			} else {
				explorerWidth = centerSplitPane.getDividerPositions()[0];
				centerSplitPane.getItems().remove(explorerScrollPane);
			}
		});
	}
}
