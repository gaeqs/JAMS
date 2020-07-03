package net.jamsimulator.jams.gui.bar;

import javafx.scene.Node;

public interface BarPane {

	/**
	 * Returns the name of this pane.
	 * This is the name inside the {@link PaneSnapshot}.
	 *
	 * @return the name.
	 */
	String getName();

	/**
	 * Returns the {@link PaneSnapshot} of this pane.
	 *
	 * @return the {@link PaneSnapshot}.
	 */
	PaneSnapshot getSnapshot();

	/**
	 * Returns the {@link Node} handled by this pane.
	 *
	 * @return the {@link Node}.
	 */
	Node getNode();

}
