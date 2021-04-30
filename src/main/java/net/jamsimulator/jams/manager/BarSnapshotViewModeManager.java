package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewMode;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePane;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePersistentWindow;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModeWindow;
import net.jamsimulator.jams.gui.bar.mode.event.BarSnapshotViewModeRegisterEvent;
import net.jamsimulator.jams.gui.bar.mode.event.BarSnapshotViewModeUnregisterEvent;

/**
 * This singleton stores all {@link BarSnapshotViewMode}s that projects may use.
 * <p>
 * To register an {@link BarSnapshotViewMode} use {@link #add(Object)}.
 * To unregister an {@link BarSnapshotViewMode} use {@link #remove(Object)}.
 * An {@link BarSnapshotViewMode}'s removal from the manager doesn't make editors to stop using
 * it inmediatelly.
 */
public class BarSnapshotViewModeManager extends Manager<BarSnapshotViewMode> {

    public static final BarSnapshotViewModeManager INSTANCE = new BarSnapshotViewModeManager();

    /**
     * Creates the manager.
     */
    public BarSnapshotViewModeManager() {
        super(BarSnapshotViewModeRegisterEvent.Before::new, BarSnapshotViewModeRegisterEvent.After::new,
                BarSnapshotViewModeUnregisterEvent.Before::new, BarSnapshotViewModeUnregisterEvent.After::new);
    }

    @Override
    protected void loadDefaultElements() {
        add(BarSnapshotViewModePane.INSTANCE);
        add(BarSnapshotViewModeWindow.INSTANCE);
        add(BarSnapshotViewModePersistentWindow.INSTANCE);
    }
}
