package net.jamsimulator.jams.gui.action;

import javafx.scene.Node;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an action that can be bind to a {@link javafx.scene.input.KeyCombination}.
 */
public abstract class Action {

	private final String name;
	private final String regionTag;
	private final String languageNode;

	/**
	 * Creates the action.
	 *
	 * @param name the name of the action. This name must be unique.
	 */
	public Action(String name, String regionTag, String languageNode) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(regionTag, "Region tag cannot be null!");
		this.name = name;
		this.regionTag = regionTag;
		this.languageNode = languageNode;
	}

	/**
	 * Returns the name of the action. This name must be unique.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the region tag of the action. Regions must have this tag to execute the action.
	 *
	 * @return the region tag.
	 */
	public String getRegionTag() {
		return regionTag;
	}

	/**
	 * Returns the language node of the action, if present.
	 * <p>
	 * This node is used when the action must be displayed on config or on context menus.
	 *
	 * @return the language node of the action, if present.
	 */
	public Optional<String> getLanguageNode() {
		return Optional.ofNullable(languageNode);
	}

	/**
	 * Executes this action.
	 *
	 * @param node the current focused node.
	 */
	public abstract void run(Node node);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Action action = (Action) o;
		return name.equals(action.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
