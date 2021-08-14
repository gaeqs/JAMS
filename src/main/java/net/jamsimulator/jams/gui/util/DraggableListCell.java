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

import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.utils.NumericUtils;

/**
 * Represents a list cell that can be dragged and dropped.
 * This allows the user to sort list views.
 *
 * @param <T> the type of the elements inside the list view.
 */
public class DraggableListCell<T> extends ListCell<T> {

    private boolean draggable = true;

    public DraggableListCell() {
        setOnDragDetected(this::onDragDetected);
        setOnDragOver(this::onDragOver);
        setOnDragEntered(this::onDragEntered);
        setOnDragExited(this::onDragExited);
        setOnDragDropped(this::onDragDropped);
        setOnDragDone(this::onDragDone);
        setCursor(Cursor.CLOSED_HAND);
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        setCursor(draggable ? Cursor.CLOSED_HAND : Cursor.DEFAULT);
    }

    protected void onDragDetected(MouseEvent event) {
        if (getItem() == null || !draggable) return;
        var dragboard = startDragAndDrop(TransferMode.MOVE);
        var content = new ClipboardContent();
        content.putString(String.valueOf(getIndex()));
        dragboard.setContent(content);

        var sceneSnapshot = JamsApplication.getScene().snapshot(null);
        int tx = (int) Math.ceil(getLocalToSceneTransform().getTx());
        int ty = (int) Math.ceil(getLocalToSceneTransform().getTy());

        var parameters = new SnapshotParameters();
        parameters.setFill(sceneSnapshot.getPixelReader().getColor(tx, ty));

        dragboard.setDragView(snapshot(parameters, null));

        event.consume();
    }

    protected void onDragOver(DragEvent event) {
        if (draggable && getItem() != null && event.getGestureSource() != this
                && event.getDragboard().hasString()
                && NumericUtils.isInteger(event.getDragboard().getString())) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    protected void onDragEntered(DragEvent event) {
        if (draggable && getItem() != null && event.getGestureSource() != this
                && event.getDragboard().hasString()
                && NumericUtils.isInteger(event.getDragboard().getString())) {
            setOpacity(0.3);
        }
        event.consume();
    }

    protected void onDragExited(DragEvent event) {
        if (draggable && getItem() != null && event.getGestureSource() != this
                && event.getDragboard().hasString()
                && NumericUtils.isInteger(event.getDragboard().getString())) {
            setOpacity(1.0);
        }
        event.consume();
    }

    protected void onDragDropped(DragEvent event) {
        if (getItem() == null || !draggable) return;
        var dragboard = event.getDragboard();

        if (dragboard.hasString()) {
            var items = getListView().getItems();

            int index = Integer.parseInt(dragboard.getString());
            if (index < 0 || index >= items.size()) return;
            items.add(getIndex(), items.remove(index));
        }
        event.consume();
    }

    protected void onDragDone(DragEvent event) {
        event.consume();
    }
}
