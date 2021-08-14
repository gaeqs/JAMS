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

package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import net.jamsimulator.jams.utils.Validate;

/**
 * Represents a pane that can hold {@link BarSnapshot snapshot}s.
 * This pane is usually linked to two {@link Bar}s.
 * <p>
 * This pane should be inside a {@link SplitPane}. This allows this pane to hide and show when needed.
 * <p>
 * This pane can contain two {@link BarSnapshot snapshot}s. Each of them is controlled by a {@link BarPaneNode}.
 */
public class BarPane extends SplitPane implements BarSnapshotHolder {

    private final BarPaneNode first, second;
    private final SplitPane parent;
    private final boolean firstInParent;

    private double dividerPosition, parentDividerPosition;

    /**
     * Creates the pane.
     *
     * @param map           the {@link BarMap} holding the {@link Bar}s linked to this pane.
     * @param parent        the {@link SplitPane} holding this pane.
     * @param firstInParent whether this pane should be added at the start or at the end of the parent {@link SplitPane}.
     * @param orientation   the orientation of this pane.
     */
    public BarPane(BarMap map, SplitPane parent, boolean firstInParent, Orientation orientation) {
        SplitPane.setResizableWithParent(this, false);

        this.parent = parent;
        this.firstInParent = firstInParent;

        first = new BarPaneNode(map, orientation == Orientation.HORIZONTAL ? parent : null);
        second = new BarPaneNode(map, orientation == Orientation.HORIZONTAL ? parent : this);
        setOrientation(orientation);

        parentDividerPosition = firstInParent ? 0.25 : 0.75;
        dividerPosition = 0.5;
    }

    /**
     * Returns the {@link BarPaneNode} that controlls the first {@link BarSnapshot snapshot}.
     *
     * @return the {@link BarPaneNode}.
     */
    public BarPaneNode getFirstNode() {
        return first;
    }

    /**
     * Returns the {@link BarPaneNode} that controlls the second {@link BarSnapshot snapshot}.
     *
     * @return the {@link BarPaneNode}.
     */
    public BarPaneNode getSecondNode() {
        return second;
    }

    @Override
    public boolean show(BarButton button) {
        Validate.notNull(button, "Button cannot be null!");
        var node = button.getBar().getPosition().shouldUseFirstPane() ? first : second;

        if (node.getButton().orElse(null) == button) return false;

        boolean empty = node.getButton().isEmpty();
        node.selectButton(button);

        if (empty) {
            getItems().add(node == first ? 0 : getItems().size(), node);
            if (getItems().size() > 1) {
                setDividerPosition(0, dividerPosition);
            } else {
                if (firstInParent) {
                    if (parent.getItems().size() > 1) {
                        double right = parent.getDividerPositions()[0];
                        parent.getItems().add(0, this);
                        parent.setDividerPosition(1, right);
                    } else {
                        parent.getItems().add(0, this);
                    }
                } else {
                    parent.getItems().add(this);
                }
                parent.setDividerPosition(firstInParent ? 0 : parent.getItems().size() - 2, parentDividerPosition);
            }
        }

        return true;
    }

    @Override
    public boolean hide(BarButton button) {
        Validate.notNull(button, "Button cannot be null!");
        var node = button.getBar().getPosition().shouldUseFirstPane() ? first : second;

        if (node.getButton().orElse(null) != button) return false;
        node.selectButton(null);

        if (getItems().size() > 1) {
            dividerPosition = getDividerPositions()[0];
        } else {
            parentDividerPosition = parent.getDividerPositions()[firstInParent ? 0 : parent.getItems().size() - 2];


            if (firstInParent && parent.getItems().size() > 2) {
                double right = parent.getDividerPositions()[1];
                parent.getItems().remove(this);
                parent.setDividerPosition(0, right);
            } else {
                parent.getItems().remove(this);
            }
        }

        getItems().remove(node);
        return true;
    }
}
