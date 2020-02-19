package net.jamsimulator.jams.gui.project;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.sidebar.Sidebar;
import net.jamsimulator.jams.project.FolderProject;
import net.jamsimulator.jams.utils.AnchorUtils;

public class FolderProjectStructurePane extends AnchorPane {

	public static final int SIDEBAR_WIDTH = 22;

	private FolderProjectTab folderProjectTab;
	private SplitPane centerSplitPane;

	public FolderProjectStructurePane(FolderProjectTab folderProjectTab) {
		this.folderProjectTab = folderProjectTab;

		centerSplitPane = new SplitPane();
		getChildren().add(centerSplitPane);
		AnchorUtils.setAnchor(centerSplitPane, 0, 0, SIDEBAR_WIDTH, 0);

		FolderProjectFolderExplorer explorer = new FolderProjectFolderExplorer(getProject().getFolder());
		ScrollPane scrollPane = new ScrollPane(explorer);
		centerSplitPane.getItems().add(scrollPane);

		TextArea area = new TextArea();
		area.setFont(new Font(FontLoader.JETBRAINS_MONO, 15));
		centerSplitPane.getItems().add(area);

		centerSplitPane.setDividerPosition(0, 0.1);

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

	private void generateLeftSidebar() {
		Sidebar sidebar = new Sidebar(true);
		AnchorUtils.setAnchor(sidebar, 0, 0, 0, -1);
		sidebar.setPrefWidth(SIDEBAR_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_WIDTH);
		getChildren().add(sidebar);

		sidebar.createSidebarButton("Project", event -> {
			System.out.println("AAAA");
		});
		sidebar.createSidebarButton("AA", event -> System.out.println("BBB"));
	}
}
