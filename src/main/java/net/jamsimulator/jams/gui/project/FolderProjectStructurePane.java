package net.jamsimulator.jams.gui.project;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
	private double explorerWidth;

	private SidePane leftPane, rightPane;

	public FolderProjectStructurePane(FolderProjectTab folderProjectTab) {
		//Black line separator
		Separator separator = new Separator(Orientation.HORIZONTAL);
		AnchorUtils.setAnchor(separator, 0, -1, 0, 0);
		getChildren().add(separator);

		this.folderProjectTab = folderProjectTab;

		//Split pane.
		centerSplitPane = new SplitPane();
		getChildren().add(centerSplitPane);
		AnchorUtils.setAnchor(centerSplitPane, 1, 0, SIDEBAR_WIDTH, SIDEBAR_WIDTH);

		//Folder explorer.
		explorer = new FolderProjectFolderExplorer(getProject().getFolder());
		explorerScrollPane = new ScrollPane(explorer);
		explorerWidth = 0.2;

		//Text area
		TextArea area = new TextArea();
		area.setFont(new Font(FontLoader.JETBRAINS_MONO, 15));
		centerSplitPane.getItems().add(area);

		//Side panes
		leftPane = new SidePane(centerSplitPane, true);
		rightPane = new SidePane(centerSplitPane, false);

		//Sidebars
		Sidebar topLeft = new Sidebar(true, true, leftPane);
		AnchorUtils.setAnchor(topLeft, 1, 100, 0, -1);
		topLeft.setPrefWidth(SIDEBAR_WIDTH);
		topLeft.setMaxWidth(SIDEBAR_WIDTH);

		topLeft.addNode("Project", explorerScrollPane);
		topLeft.addNode("TEST1", new ScrollPane(
				new Label("TEST\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nTEST!")));

		getChildren().add(topLeft);


		Sidebar bottomLeft = new Sidebar(true, false, leftPane);
		AnchorUtils.setAnchor(bottomLeft, 500, 0, 0, -1);
		bottomLeft.setPrefWidth(SIDEBAR_WIDTH);
		bottomLeft.setMaxWidth(SIDEBAR_WIDTH);

		AnchorPane test2 = new AnchorPane(new Label("BOTTOM"));

		test2.setOnMouseDragged(event -> {
			double absolute = event.getSceneY();
			double min = leftPane.getLocalToSceneTransform().getTy();
			double max = min + leftPane.getHeight();
			double relative = (absolute - min) / (max - min);
			leftPane.setDividerPosition(0, relative);
		});


		bottomLeft.addNode("TEST2", test2);
		bottomLeft.addNode("TEST3", new ScrollPane(new ImageView("https://ih1.redbubble.net/image.622548869.3699/flat,1000x1000,075,f.u2.jpg")));
		getChildren().add(bottomLeft);
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

	public SidePane getLeftPane() {
		return leftPane;
	}

	public SidePane getRightPane() {
		return rightPane;
	}
}
