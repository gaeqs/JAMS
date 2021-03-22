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
