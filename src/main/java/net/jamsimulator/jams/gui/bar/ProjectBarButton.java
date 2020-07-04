package net.jamsimulator.jams.gui.bar;

public interface ProjectBarButton {

	/**
	 * Returns the {@link ProjectBar} handling this button.
	 *
	 * @return the {@link ProjectBar}.
	 */
	ProjectBar getProjectBar();

	/**
	 * Returns the name of the button.
	 *
	 * @return the name.
	 */
	String getName();

	/**
	 * Returns the {@link ProjectPaneSnapshot} handled by this button.
	 *
	 * @return the {@link ProjectPaneSnapshot}.
	 */
	ProjectPaneSnapshot getSnapshot();

	/**
	 * Returns the {@link ProjectBarPane} handled by this button.
	 *
	 * @return the {@link ProjectBarPane}.
	 */
	ProjectBarPane getPane();
}
