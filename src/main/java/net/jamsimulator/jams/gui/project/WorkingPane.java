/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.project;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.general.JAMSShutdownEvent;
import net.jamsimulator.jams.gui.bar.*;
import net.jamsimulator.jams.gui.util.AnchorUtils;

/**
 * The default working pane. This pane contains a central {@link SplitPane},
 * the central node, the left and right {@link BarPane}s and all four {@link Bar}s.
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

    public WorkingPane(Tab parent, ProjectTab projectTab, Node center, boolean init) {
        getStyleClass().add("working-pane");
        this.parent = parent;
        this.projectTab = projectTab;
        this.center = center;
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
     * Saves all files opened by this working pane.
     */
    public abstract void saveAllOpenedFiles();

    @Override
    public void onClose() {
        barMap.forEachButton(BarButton::hide);
    }

    @Listener
    private void onShutdown(JAMSShutdownEvent.Before event) {
        onClose();
    }

    //region INIT

    protected void init() {
        //Slit panes.
        verticalSplitPane = new SplitPane();
        getChildren().add(verticalSplitPane);
        AnchorUtils.setAnchor(verticalSplitPane, 0, BOTTOM_BAR_HEIGHT, SIDEBAR_WIDTH, SIDEBAR_WIDTH);
        verticalSplitPane.setOrientation(Orientation.VERTICAL);

        horizontalSplitPane = new SplitPane();
        verticalSplitPane.getItems().add(horizontalSplitPane);

        //Center pane
        if (center == null) center = new AnchorPane();
        horizontalSplitPane.getItems().add(center);

        loadSidebars();
        Jams.getGeneralEventBroadcast().registerListeners(this, true);
    }

    private void loadSidebars() {
        // Bar Panes
        var leftPane = new BarPane(barMap, horizontalSplitPane, true, Orientation.VERTICAL);
        var rightPane = new BarPane(barMap, horizontalSplitPane, false, Orientation.VERTICAL);
        var bottomPane = new BarPane(barMap, verticalSplitPane, false, Orientation.HORIZONTAL);

        // Bars
        var leftHolder = new VBox();
        var rightHolder = new VBox();
        var bottomHolder = new HBox();
        AnchorUtils.setAnchor(leftHolder, 0, BOTTOM_BAR_HEIGHT, 0, -1);
        AnchorUtils.setAnchor(rightHolder, 0, BOTTOM_BAR_HEIGHT, -1, 0);
        AnchorUtils.setAnchor(bottomHolder, -1, 0, SIDEBAR_WIDTH, SIDEBAR_WIDTH);

        var leftTop = loadSidebar(BarPosition.LEFT_TOP, leftPane);
        var leftBottom = loadSidebar(BarPosition.LEFT_BOTTOM, leftPane);
        var rightTop = loadSidebar(BarPosition.RIGHT_TOP, rightPane);
        var rightBottom = loadSidebar(BarPosition.RIGHT_BOTTOM, rightPane);
        var bottomLeft = loadBottomBar(BarPosition.BOTTOM_LEFT, bottomPane);
        var bottomRight = loadBottomBar(BarPosition.BOTTOM_RIGHT, bottomPane);

        var leftFill = new FillRegion(BarPosition.LEFT_TOP, leftHolder, leftTop, leftBottom);
        var rightFill = new FillRegion(BarPosition.RIGHT_TOP, rightHolder, rightTop, rightBottom);
        var bottomFill = new FillRegion(BarPosition.BOTTOM_LEFT, bottomHolder, bottomLeft, bottomRight);

        leftHolder.getChildren().addAll(leftTop.getNode(), leftFill, leftBottom.getNode());
        rightHolder.getChildren().addAll(rightTop.getNode(), rightFill, rightBottom.getNode());
        bottomHolder.getChildren().addAll(bottomLeft.getNode(), bottomFill, bottomRight.getNode());
        getChildren().addAll(leftHolder, rightHolder, bottomHolder);
    }

    private Bar loadSidebar(BarPosition position, BarPane pane) {
        var bar = barMap.create(position, pane);

        bar.getNode().setPrefWidth(SIDEBAR_WIDTH);
        bar.getNode().setMaxWidth(SIDEBAR_WIDTH);
        bar.getNode().setMinHeight(100);
        return bar;
    }

    private Bar loadBottomBar(BarPosition position, BarPane pane) {
        var bar = barMap.create(position, pane);

        bar.getNode().setPrefHeight(BOTTOM_BAR_HEIGHT);
        bar.getNode().setMaxHeight(BOTTOM_BAR_HEIGHT);
        bar.getNode().setMinWidth(100);
        return bar;
    }

    //endregion

}
