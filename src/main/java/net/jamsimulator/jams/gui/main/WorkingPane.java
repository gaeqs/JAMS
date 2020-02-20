package net.jamsimulator.jams.gui.main;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.Sidebar;
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
		topLeftSidebar = loadSidebar(true, true);
		bottomLeftSidebar = loadSidebar(true, false);
		topRightSidebar = loadSidebar(false, true);
		bottomRightSidebar = loadSidebar(false, false);

		topLeftSidebar.addNode("TEST1", new AnchorPane());
		topRightSidebar.addNode("TEST2", new ScrollPane(new ImageView(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Strawberry_jam_on_a_dish.JPG/" +
						"1280px-Strawberry_jam_on_a_dish.JPG")));
		bottomLeftSidebar.addNode("TEST3", new AnchorPane(new Label("TEST!")));
		bottomRightSidebar.addNode("TEST4 LONG NAME", new TextArea());

		topLeftSidebar.addNode("TEST5", new AnchorPane());
		bottomLeftSidebar.addNode("TEST6", new AnchorPane());
		topRightSidebar.addNode("TEST7", new AnchorPane());
		bottomRightSidebar.addNode("TEST8", new AnchorPane());

		//test2.setOnMouseDragged(event -> {
		//	double absolute = event.getSceneY();
		//	double min = leftPane.getLocalToSceneTransform().getTy();
		//	double max = min + leftPane.getHeight();
		//	double relative = (absolute - min) / (max - min);
		//	leftPane.setDividerPosition(0, relative);
		//});
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
		if (top && left) {
			AnchorUtils.setAnchor(sidebar, 1, 300, 0, -1);
		} else if (top) {
			AnchorUtils.setAnchor(sidebar, 1, 300, -1, 0);
		} else if (left) {
			AnchorUtils.setAnchor(sidebar, 400, 0, 0, -1);
		} else {
			AnchorUtils.setAnchor(sidebar, 400, 0, -1, 0);
		}
		sidebar.setPrefWidth(SIDEBAR_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_WIDTH);
		getChildren().add(sidebar);
		return sidebar;
	}
}
