package net.jamsimulator.jams.gui.bar.mode;

import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.BarSnapshotHolder;
import net.jamsimulator.jams.manager.Labeled;

import java.util.Optional;

/**
 * Represents the way a {@link BarSnapshot snapshot} should be presented to the user.
 * <p>
 * Instances of this class manages the view of the snapshot when required. They also return the viewer holding
 * the node as a {@link BarSnapshotHolder}. This holder will be called when the snapshot should hide.
 */
public interface BarSnapshotViewMode extends Labeled {

    /**
     * Manages the visualization of the {@link BarSnapshot snapshot} inside the given {@link BarButton}.
     *
     * @param button the button.
     * @return the {@link BarSnapshotHolder} holding the node.
     */
    Optional<BarSnapshotHolder> manageView(BarButton button);

    /**
     * The language node of this view mode.
     * The message of this node is used in the view mode selection menu.
     *
     * @return the language node.
     */
    String getLanguageNode();
}
