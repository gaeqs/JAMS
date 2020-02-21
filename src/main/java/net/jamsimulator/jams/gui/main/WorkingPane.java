package net.jamsimulator.jams.gui.main;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.Sidebar;
import net.jamsimulator.jams.gui.sidebar.SidebarFillRegion;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * The default working pane. This pane contains a central {@link SplitPane},
 * the central node, the left and right {@link SidePane}s and all four {@link Sidebar}s.
 * <p>
 * This class is extended by the projects to add custom panes.
 */
public class WorkingPane extends AnchorPane {

	public static final int SIDEBAR_WIDTH = 25;

	private Tab parent;

	private SplitPane workingPane;
	private Node center;
	private SidePane leftPane, rightPane;
	private Sidebar topLeftSidebar, bottomLeftSidebar,
			topRightSidebar, bottomRightSidebar;

	public WorkingPane(Tab parent, Node center) {
		this.parent = parent;
		this.center = center;

		//Black line separator
		Separator separator = new Separator(Orientation.HORIZONTAL);
		AnchorUtils.setAnchor(separator, 0, -1, 0, 0);
		getChildren().add(separator);

		//Working slit pane.
		workingPane = new SplitPane();
		getChildren().add(workingPane);
		AnchorUtils.setAnchor(workingPane, 1, 0, SIDEBAR_WIDTH, SIDEBAR_WIDTH);

		//Center pane
		if (center == null) center = new AnchorPane();
		workingPane.getItems().add(center);


		//Side panes
		leftPane = new SidePane(workingPane, true);
		rightPane = new SidePane(workingPane, false);

		//Sidebars

		VBox leftSidebarHolder = new VBox();
		VBox rightSidebarHolder = new VBox();
		AnchorUtils.setAnchor(leftSidebarHolder, 1, 0, 0, -1);
		AnchorUtils.setAnchor(rightSidebarHolder, 1, 0, -1, 0);

		topLeftSidebar = loadSidebar(true, true);
		bottomLeftSidebar = loadSidebar(true, false);
		topRightSidebar = loadSidebar(false, true);
		bottomRightSidebar = loadSidebar(false, false);

		Region leftFill = new SidebarFillRegion(true, leftSidebarHolder, topLeftSidebar, bottomLeftSidebar);
		Region rightFill = new SidebarFillRegion(false, rightSidebarHolder, topRightSidebar, bottomRightSidebar);

		leftSidebarHolder.getChildren().addAll(topLeftSidebar, leftFill, bottomLeftSidebar);
		rightSidebarHolder.getChildren().addAll(topRightSidebar, rightFill, bottomRightSidebar);
		getChildren().addAll(leftSidebarHolder, rightSidebarHolder);

		topLeftSidebar.addNode("TEST1", new AnchorPane());
		topRightSidebar.addNode("TEST2", new ScrollPane(new ImageView(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Strawberry_jam_on_a_dish.JPG/" +
						"1280px-Strawberry_jam_on_a_dish.JPG")));
		bottomLeftSidebar.addNode("TEST3", new AnchorPane(new Label("TEST!")));
		bottomRightSidebar.addNode("TEST4 LONG NAME AAAAAAAAAAAAAAAAAAAAAAA", new TextArea());


		topLeftSidebar.addNode("TEST5", new AnchorPane());
		bottomLeftSidebar.addNode("TEST6", new AnchorPane());
		topRightSidebar.addNode("TEST7", new AnchorPane());
		bottomRightSidebar.addNode("TEST8", new AnchorPane());


		//Rescaling AnchorPane inside a tab. Thanks JavaFX for the bug.
		//For this workaround to work this AnchorPane must be inside another AnchorPane.
		Platform.runLater(() -> {
			getScene().heightProperty().addListener((obs, old, val) -> {
				double height = val.doubleValue() - getLocalToSceneTransform().getTy();
				setPrefHeight(height);
				setMinHeight(height);
			});

			getScene().widthProperty().addListener((obs, old, val) -> {
				double width = val.doubleValue() - getLocalToSceneTransform().getTx();
				setPrefWidth(width);
				setMinWidth(width);
			});
		});
	}


	/**
	 * Returns the {@link Tab} that contains this pane, or null.
	 *
	 * @return the {@link Tab} or null.
	 */
	public Tab getParentTab() {
		return parent;
	}

	/**
	 * Returns the central node.
	 *
	 * @return the central node.
	 */
	public Node getCenter() {
		return center;
	}

	/**
	 * Returns the left {@link SidePane}.
	 *
	 * @return the left {@link SidePane}.
	 */
	public SidePane getLeftPane() {
		return leftPane;
	}

	/**
	 * Returns the right {@link SidePane}.
	 *
	 * @return the right {@link SidePane}.
	 */
	public SidePane getRightPane() {
		return rightPane;
	}

	/**
	 * Returns the top left {@link Sidebar}.
	 *
	 * @return the top left {@link Sidebar}.
	 */
	public Sidebar getTopLeftSidebar() {
		return topLeftSidebar;
	}

	/**
	 * Returns the bottom left {@link Sidebar}.
	 *
	 * @return the bottom left {@link Sidebar}.
	 */
	public Sidebar getBottomLeftSidebar() {
		return bottomLeftSidebar;
	}

	/**
	 * Returns the top right {@link Sidebar}.
	 *
	 * @return the top right {@link Sidebar}.
	 */
	public Sidebar getTopRightSidebar() {
		return topRightSidebar;
	}

	/**
	 * Returns the bottom right {@link Sidebar}.
	 *
	 * @return the bottom right {@link Sidebar}.
	 */
	public Sidebar getBottomRightSidebar() {
		return bottomRightSidebar;
	}

	private Sidebar loadSidebar(boolean left, boolean top) {
		Sidebar sidebar = new Sidebar(left, top, left ? leftPane : rightPane);

		sidebar.setPrefWidth(SIDEBAR_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_WIDTH);
		return sidebar;
	}

}
