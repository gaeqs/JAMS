package net.jamsimulator.jams.gui.bar;

import java.util.Optional;

public interface Bar {


	/**
	 * Returns the {@link BarMap} this bar is inside of, if present.
	 *
	 * @return the {@link BarMap}, if present.
	 */
	Optional<BarMap> getBarMap();

	/**
	 * Sets the {@link BarMap} this bar is inside of.
	 * This method should only be used by a {@link BarMap}.
	 *
	 * @param barMap the {@link BarMap}.
	 */
	void setBarMap(BarMap barMap);

	/**
	 * Returns the current opened {@link BarPane}, if present.
	 *
	 * @return the {@link BarPane}.
	 */
	Optional<? extends BarPane> getCurrent();

	/**
	 * Returns the {@link BarButton} that matches the given name, if present.
	 *
	 * @param name the name.
	 * @return the {@link BarButton}, if present.
	 */
	Optional<? extends BarButton> get(String name);

	/**
	 * Removes the {@link BarPane} and the {@link BarButton} that matches the given name.
	 *
	 * @param name the name.
	 * @return whether the operation was successful.
	 */
	boolean remove(String name);

	/**
	 * Returns whether this bar contains a node whose assigned name equals the given name.
	 *
	 * @param name the given name.
	 * @return whether this sidebar contains the node.
	 */
	boolean contains(String name);

	/**
	 * Adds a node in this bar. This node will be wrapped by a {@link BarPane}.
	 * <p>
	 * If a node with the given name is already inside the bar the given node wont be added.
	 *
	 * @param snapshot the snapshot to add.
	 * @return whether the given node was added.
	 */
	boolean add(PaneSnapshot snapshot);

	/**
	 * Adds a node in this bar. This node will be wrapped by a {@link BarPane}.
	 * <p>
	 * If a node with the given name is already inside the bar the given node wont be added.
	 *
	 * @param snapshot the snapshot to add.
	 * @param index    the index of the button. This will mark the position of the button inside the bar.
	 * @return whether the given node was added.
	 * @throws IndexOutOfBoundsException when the index if out of range.
	 */
	boolean add(int index, PaneSnapshot snapshot);
}