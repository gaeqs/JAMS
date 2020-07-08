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
import net.jamsimulator.jams.gui.bar.BarMap;
import net.jamsimulator.jams.gui.bar.BarType;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
import net.jamsimulator.jams.gui.bar.bottombar.BottomBar;
import net.jamsimulator.jams.gui.bar.sidebar.SidePane;
import net.jamsimulator.jams.gui.bar.sidebar.Sidebar;
import net.jamsimulator.jams.gui.bar.sidebar.SidebarFillRegion;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.Set;

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

	protected BarMap barMap;

	protected final Set<PaneSnapshot> paneSnapshots;

	private EventHandler<WindowEvent> stageCloseListener;

	public WorkingPane(Tab parent, ProjectTab projectTab, Node center,
					   Set<PaneSnapshot> paneSnapshots, boolean init) {
		this.parent = parent;
		this.projectTab = projectTab;
		this.center = center;
		this.paneSnapshots = paneSnapshots;
		this.barMap = new BarMap();
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
	 * Returns the {@link BarMap} of this working pane.
	 * The bar map stores all sidebars and bottolm bars of the pane.
	 *
	 * @return the {@link BarMap}.
	 */
	public BarMap getBarMap() {
		return barMap;
	}

	/**
	 * Adds the given {@link PaneSnapshot} to the matching {@link Sidebar} or {@link BottomBar}.
	 *
	 * @param snapshot the {@link PaneSnapshot}.
	 * @return whether the snapshot was added. This method fails whether a snapshot with the same name is already added.
	 */
	public boolean addSidePaneSnapshot(PaneSnapshot snapshot) {
		if (!paneSnapshots.add(snapshot)) return false;
		manageSidePaneAddition(snapshot);
		return true;
	}

	@Override
	public void onClose() {
		if (stageCloseListener != null) {
			JamsApplication.removeStageCloseListener(stageCloseListener);
		}
	}

	private void manageSidePaneAddition(PaneSnapshot snapshot) {
		barMap.get(snapshot.getDefaultPosition()).ifPresent(target -> target.add(snapshot));
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
		addSnapshots();
		loadResizeEvents();

		stageCloseListener = target -> {
			stageCloseListener = null;
			onClose();
		};
		JamsApplication.addStageCloseListener(stageCloseListener);
	}

	private void loadSidebars() {
		//Side panes
		SidePane leftPane = new SidePane(horizontalSplitPane, true);
		SidePane rightPane = new SidePane(horizontalSplitPane, false);

		//Sidebars

		VBox leftSidebarHolder = new VBox();
		VBox rightSidebarHolder = new VBox();
		AnchorUtils.setAnchor(leftSidebarHolder, 1, BOTTOM_BAR_HEIGHT, 0, -1);
		AnchorUtils.setAnchor(rightSidebarHolder, 1, BOTTOM_BAR_HEIGHT, -1, 0);

		Sidebar topLeftSidebar = loadSidebar(true, true, leftPane);
		Sidebar bottomLeftSidebar = loadSidebar(true, false, leftPane);
		Sidebar topRightSidebar = loadSidebar(false, true, rightPane);
		Sidebar bottomRightSidebar = loadSidebar(false, false, rightPane);

		Region leftFill = new SidebarFillRegion(true, leftSidebarHolder, topLeftSidebar, bottomLeftSidebar);
		Region rightFill = new SidebarFillRegion(false, rightSidebarHolder, topRightSidebar, bottomRightSidebar);

		leftSidebarHolder.getChildren().addAll(topLeftSidebar, leftFill, bottomLeftSidebar);
		rightSidebarHolder.getChildren().addAll(topRightSidebar, rightFill, bottomRightSidebar);
		getChildren().addAll(leftSidebarHolder, rightSidebarHolder);

		//Bottom panes
		BottomBar bottomBar = new BottomBar(verticalSplitPane);
		AnchorUtils.setAnchor(bottomBar, -1, 0, SIDEBAR_WIDTH, SIDEBAR_WIDTH);
		bottomBar.setPrefHeight(BOTTOM_BAR_HEIGHT);
		bottomBar.setMaxHeight(BOTTOM_BAR_HEIGHT);
		getChildren().addAll(bottomBar);

		barMap.put(BarType.TOP_LEFT, topLeftSidebar);
		barMap.put(BarType.BOTTOM_LEFT, bottomLeftSidebar);
		barMap.put(BarType.TOP_RIGHT, topRightSidebar);
		barMap.put(BarType.BOTTOM_RIGHT, bottomRightSidebar);
		barMap.put(BarType.BOTTOM, bottomBar);
	}

	private Sidebar loadSidebar(boolean left, boolean top, SidePane sidePane) {
		Sidebar sidebar = new Sidebar(left, top, sidePane);

		sidebar.setPrefWidth(SIDEBAR_WIDTH);
		sidebar.setMaxWidth(SIDEBAR_WIDTH);
		return sidebar;
	}

	private void addSnapshots() {
		for (PaneSnapshot snapshot : paneSnapshots) {
			manageSidePaneAddition(snapshot);
		}
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
