package net.jamsimulator.jams.gui.bar;

/**
 * Represents a holder that manager a {@link BarSnapshot snapshot}'s visualization.
 */
public interface BarSnapshotHolder {

    /**
     * Displays the {@link BarSnapshot snapshot} represented by the given {@link BarButton button}.
     *
     * @param button the {@link BarButton button} representing the {@link BarSnapshot snapshot}.
     * @return whether this operation was successful.
     */
    boolean show(BarButton button);


    /**
     * Hides the {@link BarSnapshot snapshot} represented by the given {@link BarButton button}.
     *
     * @param button the {@link BarButton button} representing the {@link BarSnapshot snapshot}.
     * @return whether this operation was successful.
     */
    boolean hide(BarButton button);

}
