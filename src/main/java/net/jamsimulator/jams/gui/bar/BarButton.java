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
}
