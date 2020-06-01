/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.project;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bottombar.BottomBar;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.Sidebar;
import net.jamsimulator.jams.gui.sidebar.SidebarFillRegion;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.io.File;

/**
 * The default working pane. This pane contains a central {@link SplitPane},
 * the central node, the left and right {@link SidePane}s and all four {@link Sidebar}s.
 * <p>
 * This class is extended by the projects to add custom panes.
 */
public abstract class WorkingPane extends AnchorPane implements ProjectPane {

	public static final int SIDEBAR_WIDTH = 25;
	public static final int BOTTOM_BAR_HEIGHT = 25;

	protected ProjectTab projectTab;
	protected Tab parent;

	protected SplitPane horizontalSplitPane;
	protected SplitPane verticalSplitPane;
	protected Node center;
	protected SidePane leftPane, rightPane;
	protected Sidebar topLeftSidebar, bottomLeftSidebar,
			topRightSidebar, bottomRightSidebar;
	protected BottomBar bottomBar;

	private EventHandler<WindowEvent> stageCloseListener;

	public WorkingPane(Tab parent, ProjectTab projectTab, Node center, boolean init) {
		this.parent = parent;
		this.projectTab = projectTab;
		this.center = center;
		if (init) {
			init();
		}
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
	 * Returns the {@link Tab} of the project this {@link WorkingPane} manages.
	 *
	 * @return the {@link Tab}.
	 */
	public ProjectTab getProjectTab() {
		return projectTab;
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

	/**
	 * Returns the {@link BottomBar}.
	 *
	 * @return the {@link BottomBar}.
	 */
	public BottomBar getBottomBar() {
		return bottomBar;
	}

	@Override
	public void onClose() {
		if (stageCloseListener != null) {
			JamsApplication.removeStageCloseListener(stageCloseListener);
		}
	}

	//region INIT

	protected void init() {
		//Black line separator
		Separator separator = new Separator(Orientation.HORIZONTAL);
		AnchorUtils.setAnchor(separator, 0, -1, 0, 0);
		getChildren().add(separator);

		//Slit panes.
		verticalSplitPane = new SplitPane();
		getChildren().add(verticalSplitPane);
		AnchorUtils.setAnchor(verticalSplitPane, 1, BOTTOM_BAR_HEIGHT, SIDEBAR_WIDTH, SIDEBAR_WIDTH);
		verticalSplitPane.setOrientation(Orientation.VERTICAL);

		horizontalSplitPane = new SplitPane();
		verticalSplitPane.getItems().add(horizontalSplitPane);

		//Center pane
		if (center == null) center = new AnchorPane();
		horizontalSplitPane.getItems().add(center);


		loadSidebars();
		loadResizeEvents();

		stageCloseListener = target -> {
			stageCloseListener = null;
			onClose();
		};
		JamsApplication.addStageCloseListener(stageCloseListener);
	}

	private void loadSidebars() {
		//Side panes
		leftPane = new SidePane(horizontalSplitPane, true);
		rightPane = new SidePane(horizontalSplitPane, false);

		//Sidebars

		VBox leftSidebarHolder = new VBox();
		VBox rightSidebarHolder = new VBox();
		AnchorUtils.setAnchor(leftSidebarHolder, 1, BOTTOM_BAR_HEIGHT, 0, -1);
		AnchorUtils.setAnchor(rightSidebarHolder, 1, BOTTOM_BAR_HEIGHT, -1, 0);

		topLeftSidebar = loadSidebar(true, true);
		bottomLeftSidebar = loadSidebar(true, false);
		topRightSidebar = loadSidebar(false, true);
		bottomRightSidebar = loadSidebar(false, false);

		Region leftFill = new SidebarFillRegion(true, leftSidebarHolder, topLeftSidebar, bottomLeftSidebar);
		Region rightFill = new SidebarFillRegion(false, rightSidebarHolder, topRightSidebar, bottomRightSidebar);

		leftSidebarHolder.getChildren().addAll(topLeftSidebar, leftFill, bottomLeftSidebar);
		rightSidebarHolder.getChildren().addAll(topRightSidebar, rightFill, bottomRightSidebar);
		getChildren().addAll(leftSidebarHolder, rightSidebarHolder);

		//Bottom panes
		bottomBar = new BottomBar(verticalSplitPane);
		AnchorUtils.setAnchor(bottomBar, -1, 0, SIDEBAR_WIDTH, SIDEBAR_WIDTH);
		bottomBar.setPrefHeight(BOTTOM_BAR_HEIGHT);
		bottomBar.setMaxHeight(BOTTOM_BAR_HEIGHT);
		getChildren().addAll(bottomBar);
	}

	private Sidebar loadSidebar(boolean left, boolean top) {
		Sidebar sidebar = new Sidebar(left, top, left ? leftPane : rightPane);

		sidebar.setPrefWidth(SIDEBAR_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_WIDTH);
		return sidebar;
	}

	private void loadResizeEvents() {
		//Rescaling AnchorPane inside a tab. Thanks JavaFX for the bug.
		Platform.runLater(() -> {
			if (getScene() == null) {
				loadResizeEvents();
				return;
			}
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

	//endregion

}
