package net.jamsimulator.jams.gui.bar.mode;

import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.BarSnapshotHolder;

import java.util.Optional;

/**
 * Represents a {@link BarSnapshot snapshot}'s representation mode where
 * its content is placed in a pane.
 */
public class BarSnapshotViewModePane implements BarSnapshotViewMode {

    public static final String NAME = "pane";
    public static final BarSnapshotViewModePane INSTANCE = new BarSnapshotViewModePane();

    private BarSnapshotViewModePane() {
    }

    @Override
    public Optional<BarSnapshotHolder> manageView(BarButton button) {
        return button.getBar().getBarPane().show(button) ? Optional.of(button.getBar().getBarPane()) : Optional.empty();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLanguageNode() {
        return "BAR_VIEW_MODE_PANE";
    }
}
