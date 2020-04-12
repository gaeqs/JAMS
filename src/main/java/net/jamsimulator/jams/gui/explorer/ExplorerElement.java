package net.jamsimulator.jams.gui.explorer;

import net.jamsimulator.jams.gui.TaggedRegion;

import java.util.Optional;

/**
 * Represents an element inside an {@link ExplorerSection}.
 */
public interface ExplorerElement extends TaggedRegion {

	/**
	 * Returns the name of the element. This is the name shown to the user.
	 *
	 * @return the name of the element.
	 */
	String getName();

	/**
	 * Returns whether the element is selected. If true, the elements
	 * should be shown in the GUI with a blue background.
	 *
	 * @return whether the element is selected.
	 */
	boolean isSelected();

	/**
	 * Selects this element.
	 *
	 * @see #isSelected().
	 */
	void select();

	/**
	 * Deselects this element.
	 *
	 * @see #isSelected() .
	 */
	void deselect();

	/**
	 * Returns the next element in the explorer. This is the element
	 * shown below this element in the GUI.
	 *
	 * @return the next element, if present.
	 */
	Optional<ExplorerElement> getNext();

	/**
	 * Returns the previous element in the explorer. This is the element
	 * shown above this element in the GUI.
	 *
	 * @return the previous element, if present.
	 */
	Optional<ExplorerElement> getPrevious();
}
