package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import net.jamsimulator.jams.utils.Validate;

public class BarPane extends SplitPane implements BarSnapshotHolder {

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
                    parent.getItems().add(0, this);
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
            parent.getItems().remove(this);
        }

        getItems().remove(node);
        return true;
    }
}
