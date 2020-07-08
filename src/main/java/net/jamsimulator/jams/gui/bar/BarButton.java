package net.jamsimulator.jams.gui.bar;

public interface BarButton {

	/**
	 * Returns the {@link Bar} handling this button.
	 *
	 * @return the {@link Bar}.
	 */
	Bar getProjectBar();

	/**
	 * Returns the name of the button.
	 *
	 * @return the name.
	 */
	String getName();

	/**
	 * Returns the {@link PaneSnapshot} handled by this button.
	 *
	 * @return the {@link PaneSnapshot}.
	 */
	PaneSnapshot getSnapshot();

	/**
	 * Returns the {@link BarPane} handled by this button.
	 *
	 * @return the {@link BarPane}.
	 */
	BarPane getPane();

	/**
	 * Shows this pane if it was hidden, hiding any other pane.
	 * <p>
	 * If this pane is already shown, this method returns false.
	 *
	 * @return whether the operation was successful.
	 */
	boolean show();

	/**
	 * Hides this pane if it was shown.
	 * <p>
	 * If this pane is already hidden, this method returns false.
	 *
	 * @return whether the operation was successful.
	 */
	boolean hide();
}
