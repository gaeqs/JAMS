package net.jamsimulator.jams.manager;

/**
 * Represents an element with a name.
 * <p>
 * {@link Manager}s must represent an element type that implements this interface.
 */
public interface Labeled {

	/**
	 * Returns the name of the element.
	 * This name cannot be null and it must be unique!
	 *
	 * @return the name of the element.
	 */
	String getName();

}
