package net.jamsimulator.jams.gui.bar;

import javafx.scene.Node;
import javafx.scene.image.Image;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public class ProjectPaneSnapshot {

	private final String name;
	private final ProjectPaneType defaultPosition;
	private final Node node;
	private final Image icon;
	private final String languageNode;

	private ProjectPaneType currentPosition;

	public ProjectPaneSnapshot(String name, ProjectPaneType defaultPosition, Node node, Image icon, String languageNode) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(defaultPosition, "Default position cannot be null!");
		Validate.notNull(node, "Node cannot be null!");
		this.name = name;
		this.defaultPosition = defaultPosition;
		this.node = node;
		this.currentPosition = defaultPosition;
		this.icon = icon;
		this.languageNode = languageNode;
	}

	public String getName() {
		return name;
	}

	public ProjectPaneType getDefaultPosition() {
		return defaultPosition;
	}

	public Node getNode() {
		return node;
	}

	public Image getIcon() {
		return icon;
	}

	public String getLanguageNode() {
		return languageNode;
	}

	public ProjectPaneType getCurrentPosition() {
		return currentPosition;
	}

	void setCurrentPosition(ProjectPaneType currentPosition) {
		this.currentPosition = currentPosition;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectPaneSnapshot that = (ProjectPaneSnapshot) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
