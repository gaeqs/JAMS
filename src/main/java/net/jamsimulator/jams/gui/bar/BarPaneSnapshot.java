package net.jamsimulator.jams.gui.bar;

import javafx.scene.Node;
import javafx.scene.image.Image;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public class BarPaneSnapshot {

    private final String name;
    private final Node node;
    private final BarPosition defaultPosition;
    private final Image icon;
    private final String languageNode;

    public BarPaneSnapshot(String name, Node node, BarPosition defaultPosition, Image icon, String languageNode) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(node, "Node cannot be null!");
        Validate.notNull(defaultPosition, "Default position cannot be null!");
        this.name = name;
        this.node = node;
        this.defaultPosition = defaultPosition;
        this.icon = icon;
        this.languageNode = languageNode;
    }

    public String getName() {
        return name;
    }

    public Node getNode() {
        return node;
    }

    public BarPosition getDefaultPosition() {
        return defaultPosition;
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
        BarPaneSnapshot that = (BarPaneSnapshot) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
