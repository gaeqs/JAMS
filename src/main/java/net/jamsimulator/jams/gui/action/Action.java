package net.jamsimulator.jams.gui.action;

import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
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

	private final KeyCombination defaultCombination;

	/**
	 * Creates the action.
	 *
	 * @param name the name of the action. This name must be unique.
	 */
	public Action(String name, String regionTag, String languageNode, KeyCombination defaultCombination) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(regionTag, "Region tag cannot be null!");
		this.name = name;
		this.regionTag = regionTag;
		this.languageNode = languageNode;
		this.defaultCombination = defaultCombination;
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
	 * Returns the default combination of the action, if present.
	 * <p>
	 * If the combination is present, this action is not present in the actions file and
	 * no actions are bind to this combination this combination will be bind to this action.
	 * <p>
	 * The combination will not be bind if the action is present in the actions file but it has no combinations.
	 *
	 * @return the default code combination, if present.
	 */
	public Optional<KeyCombination> getDefaultCodeCombination() {
		return Optional.ofNullable(defaultCombination);
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

	@Override
	public String toString() {
		return "Action{" +
				"name='" + name + '\'' +
				", regionTag='" + regionTag + '\'' +
				", languageNode='" + languageNode + '\'' +
				", defaultCombination=" + defaultCombination +
				'}';
	}
}
