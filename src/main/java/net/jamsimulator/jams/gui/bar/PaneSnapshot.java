package net.jamsimulator.jams.gui.bar;

import javafx.scene.Node;
import javafx.scene.image.Image;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public class PaneSnapshot {

	private final String name;
	private final BarType defaultPosition;
	private final Node node;
	private final Image icon;
	private final String languageNode;

	public PaneSnapshot(String name, BarType defaultPosition, Node node, Image icon, String languageNode) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(defaultPosition, "Default position cannot be null!");
		Validate.notNull(node, "Node cannot be null!");
		this.name = name;
		this.defaultPosition = defaultPosition;
		this.node = node;
		this.icon = icon;
		this.languageNode = languageNode;
	}

	public String getName() {
		return name;
	}

	public BarType getDefaultPosition() {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PaneSnapshot that = (PaneSnapshot) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
