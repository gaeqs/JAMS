package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class BarPane extends SplitPane {

    private final BarPaneNode first, second;
    private final SplitPane parent;
    private final boolean firstInParent;

    private double dividerPosition, parentDividerPosition;

    public BarPane(BarMap map, SplitPane parent, boolean firstInParent, Orientation orientation) {
        this.parent = parent;
        this.firstInParent = firstInParent;
        first = new BarPaneNode(map, orientation == Orientation.HORIZONTAL ? parent : null);
        second = new BarPaneNode(map, orientation == Orientation.HORIZONTAL ? parent : this);
        setOrientation(orientation);

        parentDividerPosition = firstInParent ? 0.25 : 0.75;
        dividerPosition = 0.5;
    }

    public BarPaneNode getFirstNode() {
        return first;
    }

    public BarPaneNode getSecondNode() {
        return second;
    }

    public void selectFirst(BarPaneSnapshot snapshot) {
        select(first, snapshot);
    }

    public void selectSecond(BarPaneSnapshot snapshot) {
        select(second, snapshot);
    }

    private void select(BarPaneNode node, BarPaneSnapshot snapshot) {
        if (snapshot == null) {
            if (node.getSnapshot().isEmpty()) return;
            node.selectSnapshot(null);

            if (getItems().size() > 1) {
                dividerPosition = getDividerPositions()[0];
            } else {
                parentDividerPosition = parent.getDividerPositions()[firstInParent ? 0 : parent.getItems().size() - 2];
                parent.getItems().remove(this);
            }

            getItems().remove(node);

        } else {
            boolean empty = node.getSnapshot().isEmpty();
            node.selectSnapshot(snapshot);

            if (empty) {
                getItems().add(node == first ? 0 : getItems().size(), node);
                if (getItems().size() > 1) {
                    setDividerPosition(0, dividerPosition);
                } else {
                    if (firstInParent) {
                        parent.getItems().add(0, this);
                    } else {
                        parent.getItems().add(this);
                    }
                    parent.setDividerPosition(firstInParent ? 0 : parent.getItems().size() - 2, parentDividerPosition);
                }
            }
        }
    }
}
