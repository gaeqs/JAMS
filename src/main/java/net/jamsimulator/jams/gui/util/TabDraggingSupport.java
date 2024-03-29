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

package net.jamsimulator.jams.gui.util;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.editor.holder.FileOpenPosition;

import java.util.UUID;

/**
 * This class help applications to add drag and drop support for Tabs.
 * Create an instance and call {@link #addSupport(TabPane)} to add drag and drop support
 * to a {@link TabPane}.
 * <p>
 * {@link TabPane}s that uses the same dragging support can share {@link Tab}s.
 */
public class TabDraggingSupport {

    private final String draggingID = "JDS-" + UUID.randomUUID();
    private Tab current;

    /**
     * Adds drag and drop support to the given {@link TabPane}.
     * <p>
     * {@link TabPane}s that uses the same dragging support can share {@link Tab}s.
     *
     * @param tabPane the {@link TabPane}.
     */
    public void addSupport(TabPane tabPane) {
        tabPane.getTabs().forEach(this::addDragHandlers);
        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> change) -> {
            while (change.next()) {
                if (change.wasAdded())
                    change.getAddedSubList().forEach(this::addDragHandlers);
                if (change.wasRemoved())
                    change.getRemoved().forEach(this::removeDragHandlers);
            }
        });
        addDragHandlersToTabPane(tabPane);
    }

    private void addDragHandlersToTabPane(TabPane tabPane) {
        tabPane.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    current != null &&
                    (current.getTabPane() != tabPane
                            || current.getTabPane().getTabs().size() > 1 && e.getY() > 30)) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        tabPane.setOnDragDropped(e -> {

            if (draggingID.equals(e.getDragboard().getString())
                    && current != null
                    && current.getTabPane() != tabPane) {

                current.getTabPane().getTabs().remove(current);
                tabPane.getTabs().add(current);
                current.getTabPane().getSelectionModel().select(current);
            }

            //Check if it should split
            double x = e.getX();
            double y = e.getY();
            double w = tabPane.getWidth();
            double h = tabPane.getHeight();

            if (y > 30 && current instanceof FileEditorTab) {
                double nx = x / w;
                double ny = y / h;

                //CENTER
                if (nx > 0.3 && nx < 0.7 && ny > 0.3 && ny < 0.7) return;
                var bestPosition = FileOpenPosition.getBestPositionByDistance(nx, ny);
                ((FileEditorTab) current).openInNewHolder(bestPosition);

            }
        });
    }

    private void addDragHandlers(Tab tab) {
        //Moves the text to the graphic node, allowing drags.
        if (tab.getText() != null && !tab.getText().isEmpty()) {
            Label label = new Label(tab.getText(), tab.getGraphic());
            tab.setText(null);
            tab.setGraphic(label);
        }

        Node graphic = tab.getGraphic();
        graphic.setOnDragDetected(e -> {
            Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(draggingID);
            dragboard.setContent(content);


            var sceneSnapshot = JamsApplication.getScene().snapshot(null);
            int tx = (int) Math.ceil(graphic.getLocalToSceneTransform().getTx());
            int ty = (int) Math.ceil(graphic.getLocalToSceneTransform().getTy());

            var parameters = new SnapshotParameters();
            parameters.setFill(sceneSnapshot.getPixelReader().getColor(tx, ty));

            dragboard.setDragView(graphic.snapshot(parameters, null));
            current = tab;
        });
        graphic.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    current != null &&
                    current.getGraphic() != graphic) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        graphic.setOnDragDropped(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    current != null &&
                    current.getGraphic() != graphic) {

                int index = tab.getTabPane().getTabs().indexOf(tab);
                current.getTabPane().getTabs().remove(current);
                tab.getTabPane().getTabs().add(index, current);
                current.getTabPane().getSelectionModel().select(current);
            }
        });
        graphic.setOnDragDone(e -> current = null);
    }

    private void removeDragHandlers(Tab tab) {
        tab.getGraphic().setOnDragDetected(null);
        tab.getGraphic().setOnDragOver(null);
        tab.getGraphic().setOnDragDropped(null);
        tab.getGraphic().setOnDragDone(null);
    }

}
