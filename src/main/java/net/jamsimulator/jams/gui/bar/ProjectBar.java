package net.jamsimulator.jams.gui.bar;

import java.util.Optional;

public interface ProjectBar {

	/**
	 * Returns the current opened {@link ProjectBarPane}, if present.
	 *
	 * @return the {@link ProjectBarPane}.
	 */
	Optional<ProjectBarPane> getCurrent();

	/**
	 * Returns the {@link ProjectBarButton} that matches the given name, if present.
	 *
	 * @param name the name.
	 * @return the {@link ProjectBarButton}, if present.
	 */
	Optional<? extends ProjectBarButton> get(String name);

	/**
	 * Returns whether this bar contains a node whose assigned name equals the given name.
	 *
	 * @param name the given name.
	 * @return whether this sidebar contains the node.
	 */
	boolean contains(String name);

	/**
	 * Adds a node in this bar. This node will be wrapped by a {@link ProjectBarPane}.
	 * <p>
	 * If a node with the given name is already inside the bar the given node wont be added.
	 *
	 * @param snapshot the snapshot to add.
	 * @return whether the given node was added.
	 */
	boolean add(ProjectPaneSnapshot snapshot);
}
