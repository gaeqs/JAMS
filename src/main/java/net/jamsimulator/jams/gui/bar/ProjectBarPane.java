package net.jamsimulator.jams.gui.bar;

import javafx.scene.Node;

public interface ProjectBarPane {

	/**
	 * Returns the name of this pane.
	 * This is the name inside the {@link ProjectPaneSnapshot}.
	 *
	 * @return the name.
	 */
	String getName();

	/**
	 * Returns the {@link ProjectPaneSnapshot} of this pane.
	 *
	 * @return the {@link ProjectPaneSnapshot}.
	 */
	ProjectPaneSnapshot getSnapshot();

	/**
	 * Returns the {@link Node} handled by this pane.
	 *
	 * @return the {@link Node}.
	 */
	Node getNode();

}
